package org.flow.orderflow.repository;

import org.flow.orderflow.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
