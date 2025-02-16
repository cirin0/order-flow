package org.flow.orderflow.service;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.PasswordResetRequestDto;
import org.flow.orderflow.dto.PasswordResetVerifyDto;
import org.flow.orderflow.dto.user.UserLoginDto;
import org.flow.orderflow.dto.user.UserRegistrationDto;
import org.flow.orderflow.dto.user.UserSessionDto;
import org.flow.orderflow.exception.NotFound;
import org.flow.orderflow.mapper.UserMapper;
import org.flow.orderflow.model.Role;
import org.flow.orderflow.model.User;
import org.flow.orderflow.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private final Map<String, UserSessionDto> activeSessions = new HashMap<>();
  private final UserMapper userMapper;
  private final Map<String, VerificationData> passwordResetCodes = new HashMap<>();
  private final MailSenderService mailSenderService;

  public UserSessionDto registerUser(UserRegistrationDto registrationDto) {
    if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
      throw new RuntimeException("User with email: " + registrationDto.getEmail() + " already exists");
    }
    User user = userMapper.toUserRegistrationDto(registrationDto);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    if (user.getRole() == null) {
      user.setRole(Role.valueOf("USER"));
    }
    User savedUser = userRepository.save(user);
    return createUserSession(savedUser);
  }

  public UserSessionDto login(UserLoginDto loginDto) {
    User user = userRepository.findByEmail(loginDto.getEmail())
      .orElseThrow(() -> new RuntimeException("User not found with email: " + loginDto.getEmail()));
    if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
      throw new RuntimeException("Invalid password");
    }
    return createUserSession(user);
  }

  private UserSessionDto createUserSession(User user) {
    String sessionToken = generateSessionToken();
    UserSessionDto sessionDto = UserSessionDto.builder()
      .userId(user.getId())
      .email(user.getEmail())
      .sessionToken(sessionToken)
      .role(user.getRole())
      .expirationTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000)
      .build();
    activeSessions.put(sessionToken, sessionDto);
    return sessionDto;
  }

  public boolean validateSession(String sessionToken) {
    UserSessionDto session = activeSessions.get(sessionToken);
    if (session == null) {
      return false;
    }
    return session.getExpirationTime() > System.currentTimeMillis();
  }

  public void logout(String sessionToken) {
    activeSessions.remove(sessionToken);
  }

  private String generateSessionToken() {
    SecureRandom secureRandom = new SecureRandom();
    byte[] tokenBytes = new byte[32];
    secureRandom.nextBytes(tokenBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
  }

  private static class VerificationData {
    private final String code;
    private final LocalDateTime expirationTime;

    public VerificationData(String code) {
      this.code = code;
      this.expirationTime = LocalDateTime.now().plusMinutes(15);
    }

    public boolean isValid() {
      return LocalDateTime.now().isBefore(expirationTime);
    }
  }

  public void requestPasswordReset(PasswordResetRequestDto requestDto) {
    String email = requestDto.getEmail();
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

    String verificationCode = generateVerificationCode();

    passwordResetCodes.put(email, new VerificationData(verificationCode));

    String subject = "Код підтвердження для відновлення паролю";
    String message = "Ваш код підтвердження для зміни паролю: " + verificationCode +
      "\nКод діє протягом 15 хвилин.";

    mailSenderService.sendMail(email, subject, message);
  }

  public void verifyAndResetPassword(PasswordResetVerifyDto verifyDto) {
    String email = verifyDto.getEmail();
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new NotFound("User not found with email: " + email));

    VerificationData verificationData = passwordResetCodes.get(email);

    if (verificationData == null) {
      throw new RuntimeException("No password reset was requested for this email");
    }

    if (!verificationData.isValid()) {
      passwordResetCodes.remove(email);
      throw new RuntimeException("Verification code has expired. Please request a new one");
    }

    if (!verificationData.code.equals(verifyDto.getVerificationCode())) {
      throw new RuntimeException("Invalid verification code");
    }

    user.setPassword(passwordEncoder.encode(verifyDto.getNewPassword()));
    userRepository.save(user);
    passwordResetCodes.remove(email);
  }

  public String generateVerificationCode() {
    Random random = new Random();
    int code = 100000 + random.nextInt(900000);
    return String.valueOf(code);
  }
}
