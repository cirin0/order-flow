package org.flow.orderflow.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.user.UserLoginDto;
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
  @Operation(summary = "Register a new user", description = "Register a new user and return JWT token")
  public ResponseEntity<Map<String, Object>> register(@RequestBody UserRegistrationDto registrationDto) {
    UserSessionDto session = authenticationService.registerUser(registrationDto);

    Map<String, Object> response = new HashMap<>();
    response.put("user", session);
    response.put("token", session.getSessionToken());
    response.put("tokenType", "Bearer");

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/login")
  @Operation(summary = "Login user", description = "Authenticate user and return JWT token")
  public ResponseEntity<Map<String, Object>> login(@RequestBody UserLoginDto userLoginDto) {
    UserSessionDto session = authenticationService.login(userLoginDto);

    Map<String, Object> response = new HashMap<>();
    response.put("user", session);
    response.put("token", session.getSessionToken());
    response.put("tokenType", "Bearer");

    return ResponseEntity.ok(response);
  }

  @PostMapping("/validate")
  @Operation(summary = "Validate token", description = "Validate JWT token")
  public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7); // Remove "Bearer " prefix

    if (authenticationService.validateSession(token)) {
      String username = jwtUtil.extractUsername(token);
      String role = jwtUtil.extractRole(token);

      Map<String, Object> response = new HashMap<>();
      response.put("valid", true);
      response.put("username", username);
      response.put("role", role);

      return ResponseEntity.ok(response);
    } else {
      Map<String, Object> response = new HashMap<>();
      response.put("valid", false);

      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
  }

  // No server-side logout needed for JWT, but keeping endpoint for API compatibility
  @PostMapping("/logout")
  @Operation(summary = "Logout", description = "Client should discard the JWT token")
  public ResponseEntity<Void> logout() {
    // No server-side action needed for JWT
    return ResponseEntity.ok().build();
  }
}
