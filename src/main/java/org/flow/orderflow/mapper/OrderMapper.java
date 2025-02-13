package org.flow.orderflow.mapper;

import org.flow.orderflow.dto.order.OrderDto;
import org.flow.orderflow.dto.order.OrderItemDto;
import org.flow.orderflow.model.Order;
import org.flow.orderflow.model.OrderItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

  @Mapping(target = "items", source = "items")
  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "userEmail", source = "user.email")
  @Mapping(target = "userFirstName", source = "user.first_name")
  @Mapping(target = "userLastName", source = "user.last_name")
  @Mapping(target = "statusDescription", source = "status.description")
  OrderDto toDto(Order order);

  List<OrderDto> toDtoList(List<Order> orders);

  List<OrderItemDto> toOrderItemDtoList(List<OrderItemDto> items);

  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "productName", source = "product.name")
  @Mapping(target = "price", source = "product.price")
  @Mapping(target = "totalPrice", expression = "java(orderItem.getQuantity() * orderItem.getProduct().getPrice())")
  OrderItemDto toDto(OrderItem orderItem);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Order partialUpdate(OrderDto orderDto, @MappingTarget Order order);
}
