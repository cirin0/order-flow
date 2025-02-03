package org.flow.orderflow.service;

import lombok.AllArgsConstructor;
import org.flow.orderflow.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;
}
