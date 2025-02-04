package org.flow.orderflow.service;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;
}
