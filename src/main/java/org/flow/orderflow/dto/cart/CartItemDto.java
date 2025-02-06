package org.flow.orderflow.dto.cart;

import lombok.Data;

@Data
public class CartItemDto {
  private Long id;
  private Long productId;
  private Integer quantity;
  private Double price;
}
