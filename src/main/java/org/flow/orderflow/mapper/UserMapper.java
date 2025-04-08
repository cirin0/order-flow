package org.flow.orderflow.mapper;

import org.flow.orderflow.dto.user.UserDto;
import org.flow.orderflow.dto.user.UserRegistrationDto;
import org.flow.orderflow.model.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
  @Mapping(target = "firstName", source = "first_name")
  @Mapping(target = "lastName", source = "last_name")
  @Mapping(source = "deliveryAddress", target = "address")
  UserDto toDto(User user);

  User toEntity(UserDto dto);

  @Mapping(target = "first_name", source = "firstName")
  @Mapping(target = "last_name", source = "lastName")
  User toUserRegistrationDto(UserRegistrationDto dto);

  @Mapping(source = "first_name", target = "firstName")
  @Mapping(source = "last_name", target = "lastName")
  UserRegistrationDto toUserRegistrationDto(User user);

  @Mapping(target = "first_name", source = "firstName")
  @Mapping(target = "last_name", source = "lastName")
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  User partialUpdate(UserDto userDTO, @MappingTarget User user);

}
