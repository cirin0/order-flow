package org.flow.orderflow.service;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.user.UserLoginDto;
import org.flow.orderflow.dto.user.UserRegisteredDto;
import org.flow.orderflow.dto.user.UserRegistrationDto;
import org.flow.orderflow.dto.user.UserSessionDto;
import org.flow.orderflow.exception.InvalidRefreshToken;
import org.flow.orderflow.mapper.UserMapper;
import org.flow.orderflow.model.Role;
import org.flow.orderflow.model.User;
import org.flow.orderflow.repository.UserRepository;
import org.flow.orderflow.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;

  public UserRegisteredDto registerUser(UserRegistrationDto registrationDto) {
    if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
      throw new RuntimeException("User with email: " + registrationDto.getEmail() + " already exists");
    }
    User user = userMapper.toUserRegistrationDto(registrationDto);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    if (user.getRole() == null) {
      user.setRole(Role.valueOf("USER"));
    }

    User savedUser = userRepository.save(user);
    return UserRegisteredDto.builder()
      .firstName(savedUser.getFirst_name())
      .lastName(savedUser.getLast_name())
      .email(savedUser.getEmail())
      .build();
  }

  public UserSessionDto loginUser(UserLoginDto loginDto) {
    try {
      Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
      );

      if (authentication.isAuthenticated()) {
        User user = userRepository.findByEmail(loginDto.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getRole());

        return UserSessionDto.builder()
          .userId(user.getId())
          .email(user.getEmail())
          .accessToken(accessToken)
          .refreshToken(refreshToken)
          .role(user.getRole())
          .expirationTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000)
          .refreshExpirationTime(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000) // 30 days
          .build();
      } else {
        throw new RuntimeException("Invalid credentials");
      }
    } catch (AuthenticationException e) {
      throw new RuntimeException("Authentication failed: " + e.getMessage());
    }
  }

  public boolean validateSession(String token) {
    try {
      String username = jwtUtil.extractUsername(token);
      return username != null && !jwtUtil.isTokenExpired(token) && jwtUtil.isAccessToken(token);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean validateRefreshToken(String refreshToken) {
    try {
      String username = jwtUtil.extractUsername(refreshToken);
      return username != null && !jwtUtil.isTokenExpired(refreshToken) && jwtUtil.isRefreshToken(refreshToken);
    } catch (Exception e) {
      return false;
    }
  }

  public UserSessionDto refreshAccessToken(String refreshToken) {
    if (!validateRefreshToken(refreshToken)) {
      throw new InvalidRefreshToken("Token is not valid");
    }

    String username = jwtUtil.extractUsername(refreshToken);
    User user = userRepository.findByEmail(username)
      .orElseThrow(() -> new RuntimeException("User not found"));

    String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

    return UserSessionDto.builder()
      .userId(user.getId())
      .email(user.getEmail())
      .accessToken(newAccessToken)
      .refreshToken(refreshToken)
      .role(user.getRole())
      .expirationTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000)
      .refreshExpirationTime(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)
      .build();
  }

  public void logout(String token) {
  }
}
