package org.flow.orderflow.service;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.user.UserLoginDto;
import org.flow.orderflow.dto.user.UserRegistrationDto;
import org.flow.orderflow.dto.user.UserSessionDto;
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

    String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole());

    return UserSessionDto.builder()
      .userId(savedUser.getId())
      .email(savedUser.getEmail())
      .sessionToken(token)
      .role(savedUser.getRole())
      .expirationTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000) // Keep for backward compatibility
      .build();
  }

  public UserSessionDto login(UserLoginDto loginDto) {
    try {
      Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
      );

      if (authentication.isAuthenticated()) {
        User user = userRepository.findByEmail(loginDto.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return UserSessionDto.builder()
          .userId(user.getId())
          .email(user.getEmail())
          .sessionToken(token)
          .role(user.getRole())
          .expirationTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000) // Keep for backward compatibility
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
      return username != null && !jwtUtil.isTokenExpired(token);
    } catch (Exception e) {
      return false;
    }
  }

  // No need for logout with JWT as it's stateless
  // Client should just remove the token
  public void logout(String token) {
    // No server-side action needed for JWT
  }
}
