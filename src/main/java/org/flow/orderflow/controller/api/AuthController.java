package org.flow.orderflow.controller.api;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.user.UserDto;
import org.flow.orderflow.dto.user.UserLoginDto;
import org.flow.orderflow.dto.user.UserRegistrationDto;
import org.flow.orderflow.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final UserService userService;

  @PostMapping("/login")
  public ResponseEntity<UserDto> login(@RequestBody UserLoginDto dto) {
    return ResponseEntity.ok(userService.loginUser(dto));
  }

  @PostMapping("/register")
  public ResponseEntity<UserDto> register(@RequestBody UserRegistrationDto dto) {
    return ResponseEntity.ok(userService.registerUser(dto));
  }

//  @PostMapping("/logout")
//  public ResponseEntity<Boolean> logout(@RequestBody UserDto dto) {
//    return ResponseEntity.ok(userService.logoutUser(dto));
//  }
}
