package org.flow.orderflow.dto.order;

import lombok.Data;
import org.flow.orderflow.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
  private Long id;
  private Long userId;
  private String userEmail;        // Додано поле для email користувача
  private String userFirstName;    // Додано поле для імені користувача
  private String userLastName;     // Додано поле для прізвища користувача
  private List<OrderItemDto> items;
  private Double totalPrice;
  private LocalDateTime orderDate;
  private OrderStatus status;
  private String statusDescription;
  private String orderNumber;
  private DeliveryAddressDto deliveryAddress;

}
