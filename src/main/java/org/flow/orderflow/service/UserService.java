package org.flow.orderflow.service;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.user.UserDto;
import org.flow.orderflow.exception.NotFound;
import org.flow.orderflow.mapper.UserMapper;
import org.flow.orderflow.model.Role;
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

  public String changeRoleAdmin(Long id) {
    User user = userRepository.findById(id)
      .orElseThrow(() -> new NotFound("User not found with id: " + id));
    user.setRole(Role.valueOf("ADMIN"));
    userRepository.save(user);
    return "User role changed to ADMIN";
  }

  public boolean existsByEmail(String email) {
    return userRepository.findByEmail(email).isPresent();
  }
}
