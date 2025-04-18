package org.flow.orderflow.dto.user;

import lombok.Data;

@Data
public class UserDto {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String role;
  private String phone;
  private AddressDto address;
}
