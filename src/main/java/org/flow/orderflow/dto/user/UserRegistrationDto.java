package org.flow.orderflow.dto.user;

import lombok.Data;
import org.flow.orderflow.model.Role;

@Data
public class UserRegistrationDto {
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private Role role;
}
