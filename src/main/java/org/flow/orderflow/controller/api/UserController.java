package org.flow.orderflow.controller.api;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.user.AddressDto;
import org.flow.orderflow.dto.user.UserDto;
import org.flow.orderflow.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping
  public ResponseEntity<List<UserDto>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
    return ResponseEntity.ok(userService.getUserByEmail(email));
  }

  @GetMapping("/check/{email}")
  public ResponseEntity<Boolean> checkUserExists(@PathVariable String email) {
    return ResponseEntity.ok(userService.existsByEmail(email));
  }

  @PostMapping("/{id}")
  public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
    return ResponseEntity.ok(userService.updateUser(id, userDto));
  }

  @PostMapping("/email/{email}")
  public ResponseEntity<UserDto> updateUserByEmail(@PathVariable String email, @RequestBody UserDto userDto) {
    return ResponseEntity.ok(userService.updateUserByEmail(email, userDto));
  }

  @Hidden
  @PostMapping("/admin/{id}")
  public ResponseEntity<String> changeRoleAdmin(@PathVariable Long id) {
    return ResponseEntity.ok(userService.changeRoleAdmin(id));
  }

  @PostMapping("/{userId}/address")
  public ResponseEntity<AddressDto> addOrUpdateAddress(@PathVariable Long userId,
                                                       @RequestBody AddressDto addressDto) {
    return ResponseEntity.ok(userService.addOrUpdateDeliveryAddress(userId, addressDto));
  }
}
