package org.flow.orderflow.mapper;

import org.flow.orderflow.dto.user.AddressDto;
import org.flow.orderflow.model.Address;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressMapper {
  AddressDto toDto(Address address);

  Address toEntity(AddressDto addressDto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Address partialUpdate(AddressDto addressDto, @MappingTarget Address address);
}
