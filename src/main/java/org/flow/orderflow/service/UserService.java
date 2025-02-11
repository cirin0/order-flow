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


  public UserDto updateUser(Long id, UserDto userDto) {
    User existingUser = userRepository.findById(id)
      .orElseThrow(() -> new NotFound("User not found with id: " + id));

    // Перевіряємо номер телефону на унікальність, якщо він змінюється
    if (userDto.getPhone() != null && !userDto.getPhone().equals(existingUser.getPhone())) {
      if (userRepository.existsByPhoneAndIdNot(userDto.getPhone(), id)) {
        throw new IllegalStateException("Номер телефону вже використовується");
      }
      existingUser.setPhone(userDto.getPhone());
    }

    // Оновлюємо інші поля
    userMapper.partialUpdate(userDto, existingUser);

// Зберігаємо користувача
    User savedUser = userRepository.save(existingUser);

// Повертаємо оновленого користувача у вигляді DTO
    return userMapper.toDto(savedUser);

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
