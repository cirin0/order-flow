package org.flow.orderflow.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.password.ForgotPasswordDto;
import org.flow.orderflow.dto.password.ResetPasswordDto;
import org.flow.orderflow.dto.password.ValidateCodeDto;
import org.flow.orderflow.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/passwords")
@RequiredArgsConstructor
public class PasswordResetController {
  private final PasswordResetService passwordResetService;

  @PostMapping("/forgot")
  public ResponseEntity<String> initiatePasswordReset(@Valid @RequestBody ForgotPasswordDto request) {
    try {
      passwordResetService.initiatePasswordReset(request);
      return ResponseEntity.ok("Password reset code sent to your email");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/validate-code")
  public ResponseEntity<Boolean> validateResetCode(@Valid @RequestBody ValidateCodeDto request) {
    boolean isValid = passwordResetService.validateResetCode(request);
    return ResponseEntity.ok(isValid);
  }

  @PostMapping("/reset")
  public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordDto request) {
    try {
      if (!request.isPasswordsMatch()) {
        return ResponseEntity.badRequest().body("Passwords do not match");
      }
      passwordResetService.resetPassword(request);
      return ResponseEntity.ok("Password successfully reset");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
