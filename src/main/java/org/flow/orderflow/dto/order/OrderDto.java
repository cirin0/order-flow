package org.flow.orderflow.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flow.orderflow.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
  private Long id;
  private Long userId;
  private String userEmail;
  private String userFirstName;
  private String userLastName;
  private List<OrderItemDto> items;
  private Double totalPrice;
  private LocalDateTime orderDate;
  private OrderStatus status;
  private String statusDescription;
  private String orderNumber;
  private DeliveryAddressDto deliveryAddress;
}
