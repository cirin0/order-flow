package org.flow.orderflow.dto.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordDto(
  @NotBlank @Email String email,
  @NotBlank @Size(min = 6, max = 6) String code,
  @NotBlank @Size(min = 8, max = 32) String newPassword,
  @NotBlank String confirmPassword
) {
  public boolean isPasswordsMatch() {
    return newPassword.equals(confirmPassword);
  }
}
