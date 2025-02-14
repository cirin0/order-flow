//package org.flow.orderflow.mapper;
//
//import org.flow.orderflow.dto.order.OrderDetailsDto;
//import org.flow.orderflow.model.OrderDetails;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//@Mapper(componentModel = "spring")
//public interface OrderDetailsMapper {
//  @Mapping(target = "id", source = "id")
//  OrderDetailsDto toDto(OrderDetails orderDetails);
//
//  @Mapping(target = "id", source = "id")
//  @Mapping(target = "order", ignore = true)
//  OrderDetails toEntity(OrderDetailsDto dto);
//}
