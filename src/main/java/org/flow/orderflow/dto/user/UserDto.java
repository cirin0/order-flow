package org.flow.orderflow.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class UserDto {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  @JsonIgnore
  private String password;
  @JsonIgnore
  private String role;
  private String phone;
  private AddressDto address;
}
