package org.flow.orderflow.service;

import org.flow.orderflow.model.Order;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;

  @Transactional
  public Order saveOrder(Order order) {
    return orderRepository.save(order);
  }
}
