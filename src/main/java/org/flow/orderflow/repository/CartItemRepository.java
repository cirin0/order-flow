package org.flow.orderflow.repository;

import org.flow.orderflow.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  void deleteAllByProductId(Long id);
}
