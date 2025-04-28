package org.flow.orderflow.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.user.UserLoginDto;
import org.flow.orderflow.dto.user.UserRegisteredDto;
import org.flow.orderflow.dto.user.UserRegistrationDto;
import org.flow.orderflow.dto.user.UserSessionDto;
import org.flow.orderflow.security.JwtUtil;
import org.flow.orderflow.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API with JWT")
public class AuthenticationController {
  private final AuthenticationService authenticationService;
  private final JwtUtil jwtUtil;

  @PostMapping("/register")
  @Operation(summary = "Register a new user", description = "Register a new user")
  public ResponseEntity<UserRegisteredDto> register(@RequestBody UserRegistrationDto registrationDto) {
    return ResponseEntity.ok(authenticationService.registerUser(registrationDto));
  }

  @PostMapping("/login")
  @Operation(summary = "Login user", description = "Authenticate user and return JWT tokens")
  public ResponseEntity<UserSessionDto> login(@RequestBody UserLoginDto userLoginDto) {
    return ResponseEntity.ok(authenticationService.loginUser(userLoginDto));
  }

  @PostMapping("/validate")
  @Operation(summary = "Validate token", description = "Validate JWT token")
  public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7);

    try {
      Map<String, Object> response = new HashMap<>();
      response.put("valid", !jwtUtil.isTokenExpired(token));
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, Object> response = new HashMap<>();
      response.put("message", e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
  }

  @PostMapping("/refresh-token")
  @Operation(summary = "Refresh token", description = "Generate a new access token using a valid refresh token")
  public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> request) {
    String refreshToken = request.get("refreshToken");
    try {
      UserSessionDto session = authenticationService.refreshAccessToken(refreshToken);
      Map<String, Object> response = new HashMap<>();
      response.put("user", session);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, Object> response = new HashMap<>();
      response.put("message", e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
  }
}
