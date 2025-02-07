package org.flow.orderflow.mapper;

import org.flow.orderflow.dto.user.UserDto;
import org.flow.orderflow.dto.user.UserLoginDto;
import org.flow.orderflow.dto.user.UserRegistrationDto;
import org.flow.orderflow.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
  @Mapping(target = "firstName", source = "first_name")
  @Mapping(target = "lastName", source = "last_name")
  UserDto toDto(User user);

  User toEntity(UserDto dto);

  @Mapping(target = "first_name", source = "firstName")
  @Mapping(target = "last_name", source = "lastName")
  User toUserRegistrationDto(UserRegistrationDto dto);

  @Mapping(target = "firstName", source = "first_name")
  @Mapping(target = "lastName", source = "last_name")
  UserRegistrationDto toRegistrationEntity(User user);

  User toUserLoginDto(UserLoginDto dto);

  UserLoginDto toLoginEntity(User user);

//  @Mapping(target = "first_name", source = "firstName")
//  @Mapping(target = "last_name", source = "lastName")
////  @Mapping(target = "role", constant = "Role.USER")
//  @Mapping(target = "orders", ignore = true)
//  @Mapping(target = "createdAt", ignore = true)
//  @Mapping(target = "updatedAt", ignore = true)
//  User toEntity(UserRegistrationDto dto);
}
