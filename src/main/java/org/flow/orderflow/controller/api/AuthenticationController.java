package org.flow.orderflow.controller.api;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.user.UserLoginDto;
import org.flow.orderflow.dto.user.UserRegistrationDto;
import org.flow.orderflow.dto.user.UserSessionDto;
import org.flow.orderflow.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authentication")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService authenticationService;

  @PostMapping("/register")
  public ResponseEntity<UserSessionDto> register(@RequestBody UserRegistrationDto registrationDto) {
    UserSessionDto session = authenticationService.registerUser(registrationDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(session);
  }

  @PostMapping("/login")
  public ResponseEntity<UserSessionDto> login(@RequestBody UserLoginDto userLoginDto) {
    UserSessionDto session = authenticationService.login(userLoginDto);
    return ResponseEntity.ok(session);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@RequestHeader("Authorization") String sessionToken) {
    authenticationService.logout(sessionToken);
    return ResponseEntity.ok().build();
  }

}
