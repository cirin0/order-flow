package org.flow.orderflow.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.user.UserDto;
import org.flow.orderflow.dto.user.UserRegistrationDto;
import org.flow.orderflow.mapper.UserMapper;
import org.flow.orderflow.model.User;
import org.flow.orderflow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// UserService.java
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final CartService cartService;



  @Transactional
  public UserDto registerUser(UserRegistrationDto dto) {
    if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
      throw new IllegalArgumentException("User already exists");
    }
    User user = userMapper.toEntity(dto);
    user = userRepository.save(user);
    cartService.createCartForUser(user);
    return userMapper.toDTO(user);
  }




  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public boolean existsByEmail(String email) {
    return userRepository.findByEmail(email).isPresent();
  }
}
