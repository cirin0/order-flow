package org.flow.orderflow.mapper;

import org.flow.orderflow.dto.user.UserDto;
import org.flow.orderflow.dto.user.UserRegistrationDto;
import org.flow.orderflow.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
  @Mapping(target = "firstName", source = "first_name")
  @Mapping(target = "lastName", source = "last_name")
  UserDto toDTO(User user);

  @Mapping(target = "first_name", source = "firstName")
  @Mapping(target = "last_name", source = "lastName")
//  @Mapping(target = "role", constant = "Role.USER")
  @Mapping(target = "orders", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  User toEntity(UserRegistrationDto dto);
}
