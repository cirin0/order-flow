package org.flow.orderflow.repository;

import org.flow.orderflow.model.Cart;
import org.flow.orderflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
  Optional<Cart> findByUser(User user);
  Optional<Cart> findByUserId(Long userId);

}
