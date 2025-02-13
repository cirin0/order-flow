package org.flow.orderflow.repository;

import org.flow.orderflow.model.Order;
import org.flow.orderflow.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findByUserId(Long userId);

  List<Order> findByStatusAndOrderDateBefore(OrderStatus status, LocalDateTime dateTime);

  Optional<Order> findByUserEmail(String email);
}
