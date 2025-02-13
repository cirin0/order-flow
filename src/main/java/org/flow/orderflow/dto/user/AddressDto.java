package org.flow.orderflow.dto.user;

import lombok.Data;

@Data
public class AddressDto {
  private Long id;
  private String region;
  private String city;
  private String area;
  private String street;
  private String apartment;
  private String house;
}
