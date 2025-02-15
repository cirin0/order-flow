package org.flow.orderflow.mapper;

import org.flow.orderflow.dto.order.DeliveryAddressDto;
import org.flow.orderflow.model.DeliveryAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface DeliveryAddressMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "order", ignore = true)
  DeliveryAddress toEntity(DeliveryAddressDto dto);

  DeliveryAddressDto toDto(DeliveryAddress entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "order", ignore = true)
  void updateEntity(DeliveryAddressDto dto, @MappingTarget DeliveryAddress entity);
}
