package org.flow.orderflow.repository;

import org.flow.orderflow.model.Order;
import org.flow.orderflow.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findByUserId(Long userId);

  List<Order> findByStatusAndOrderDateBefore(OrderStatus status, LocalDateTime dateTime);

  List<Order> findByUserEmail(String email);

  boolean existsByOrderNumber(String string);
}
