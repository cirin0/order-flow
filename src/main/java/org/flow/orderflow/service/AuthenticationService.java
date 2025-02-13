package org.flow.orderflow.service;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.user.UserLoginDto;
import org.flow.orderflow.dto.user.UserRegistrationDto;
import org.flow.orderflow.dto.user.UserSessionDto;
import org.flow.orderflow.mapper.UserMapper;
import org.flow.orderflow.model.Role;
import org.flow.orderflow.model.User;
import org.flow.orderflow.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private final Map<String, UserSessionDto> activeSessions = new HashMap<>();
  private final UserMapper userMapper;

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
}
