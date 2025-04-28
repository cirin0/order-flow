package org.flow.orderflow.dto.user;

import lombok.Builder;
import lombok.Data;
import org.flow.orderflow.model.Role;

@Data
@Builder
public class UserSessionDto {
  private Long userId;
  private String email;
  private Role role;
  private String accessToken;
  private String refreshToken;
  private Long expirationTime;
  private Long refreshExpirationTime;
}
