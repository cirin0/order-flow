package org.flow.orderflow.service;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.user.UserDto;
import org.flow.orderflow.dto.user.UserLoginDto;
import org.flow.orderflow.dto.user.UserRegistrationDto;
import org.flow.orderflow.exception.NotFound;
import org.flow.orderflow.mapper.UserMapper;
import org.flow.orderflow.model.User;
import org.flow.orderflow.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final CartService cartService;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public UserDto getUserByEmail(String email) {
    return userRepository.findByEmail(email)
      .map(userMapper::toDto)
      .orElseThrow(() -> new NotFound("User not found with email: " + email));
  }

  public UserDto getUserById(Long id) {
    User user = userRepository.findById(id)
      .orElseThrow(() -> new NotFound("User not found with id: " + id));
    return userMapper.toDto(user);
  }

  public List<UserDto> getAllUsers() {
    return userRepository.findAll().stream()
      .map(userMapper::toDto)
      .collect(Collectors.toList());
  }

//  @Transactional
//  public UserDto registerUser(UserRegistrationDto dto) {
//    User user = userMapper.toUserRegistrationDto(dto);
//    userRepository.save(user);
//    cartService.createCartForUser(user);
//    return userMapper.toDto(user);
//  }

  public UserDto registerUser(UserRegistrationDto dto) {
    if (existsByEmail(dto.getEmail())) {
      throw new RuntimeException("User with email: " + dto.getEmail() + " already exists");
    }
    User user = userMapper.toUserRegistrationDto(dto);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);
    cartService.createCartForUser(user);
    return userMapper.toDto(user);
  }

  public boolean existsByEmail(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  public UserDto loginUser(UserLoginDto dto) {
    User user = userRepository.findByEmail(dto.getEmail())
      .orElseThrow(() -> new NotFound("User not found with email: " + dto.getEmail()));
    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
      throw new RuntimeException("Invalid password");
    }
    return userMapper.toDto(user);
  }

//   Не використовується, не видаляти

//  public Boolean logoutUser(UserDto dto) {
//    return true;
//  }


}
