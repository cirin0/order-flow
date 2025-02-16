package org.flow.orderflow.dto.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ValidateCodeDto(
  @NotBlank @Email String email,
  @NotBlank @Size(min = 6, max = 6) String code
) {
}
