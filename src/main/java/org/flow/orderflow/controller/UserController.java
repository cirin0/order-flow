package org.flow.orderflow.controller;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.user.UserDto;
import org.flow.orderflow.dto.user.UserRegistrationDto;
import org.flow.orderflow.mapper.UserMapper;
import org.flow.orderflow.model.User;
import org.flow.orderflow.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// UserController.java
@RestController


@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @PostMapping("/register")
  public ResponseEntity<UserDto> registerUser(@RequestBody UserRegistrationDto dto) {
    return ResponseEntity.ok(userService.registerUser(dto));
  }

  @GetMapping("/check/{email}")
  public ResponseEntity<Boolean> checkUserExists(@PathVariable String email) {
    return ResponseEntity.ok(userService.existsByEmail(email));
  }
}
