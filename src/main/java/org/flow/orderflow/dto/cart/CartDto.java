package org.flow.orderflow.dto.cart;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartDto {
  private Long id;
  private Long userId;
  private List<CartItemDto> items;
  private Double totalPrice;
  private List<String> warningMessages;
}
