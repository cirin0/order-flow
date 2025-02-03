package org.flow.orderflow.service;

import lombok.AllArgsConstructor;
import org.flow.orderflow.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
  public final UserRepository userRepository;

}
