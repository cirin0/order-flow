package org.flow.orderflow.dto.user;

import lombok.Data;

@Data
public class UserLoginDto {
  private String email;
  private String password;
}
