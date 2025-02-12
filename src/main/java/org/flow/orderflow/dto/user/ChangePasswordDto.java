package org.flow.orderflow.dto.user;

import lombok.Data;

@Data
public class ChangePasswordDto {
  private String currentPassword;
  private String newPassword;
}
