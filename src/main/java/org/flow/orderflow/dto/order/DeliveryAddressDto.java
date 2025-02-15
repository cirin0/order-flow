package org.flow.orderflow.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddressDto {
  private Long id;
  private String region;
  private String city;
  private String area;
  private String street;
  private String house;
  private String apartment;
  private String postOffice;
}

