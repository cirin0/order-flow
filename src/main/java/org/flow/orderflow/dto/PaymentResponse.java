package org.flow.orderflow.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
  private String clientSecret;
  private String publishableKey;
  private Double amount;
  private String currency;
  private String status;
  private Long orderId;
}
