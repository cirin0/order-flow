package org.flow.orderflow.repository;

import org.flow.orderflow.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
  void deleteAllByProductId(Long productId);
}
