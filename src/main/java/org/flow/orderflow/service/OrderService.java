package org.flow.orderflow.service;

import lombok.AllArgsConstructor;
import org.flow.orderflow.model.Order;
import org.flow.orderflow.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;

  @Transactional
  public Order saveOrder(Order order) {
    return orderRepository.save(order);
  }
}
