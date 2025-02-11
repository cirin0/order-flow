package org.flow.orderflow.mapper;

import org.flow.orderflow.dto.user.UserDto;
import org.flow.orderflow.dto.user.UserLoginDto;
import org.flow.orderflow.dto.user.UserRegistrationDto;
import org.flow.orderflow.dto.user.UserSessionDto;
import org.flow.orderflow.model.User;
import org.mapstruct.*;

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

  User toUserSessionDto(UserSessionDto dto);

  @Mapping(target = "userId", source = "id")
  @Mapping(target = "role", source = "role")
  UserSessionDto toSessionEntity(User user);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  User partialUpdate(UserDto userDTO, @MappingTarget User user);

}
