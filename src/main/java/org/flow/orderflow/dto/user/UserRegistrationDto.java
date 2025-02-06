package org.flow.orderflow.dto.user;
import lombok.Data;

@Data
public class UserRegistrationDto {
  private String firstName;
  private String lastName;
  private String email;
  private String password;
}
