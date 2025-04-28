package org.flow.orderflow.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegisteredDto {
  private String firstName;
  private String lastName;
  private String email;
}
