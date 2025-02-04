package org.flow.orderflow.repository;

import org.flow.orderflow.model.Cart;
import org.flow.orderflow.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
  @Query("SELECT c FROM Cart c WHERE c.user.id = :userId")
  Optional<Cart> findByUserId(@Param("userId") Long userId);

  @Query("SELECT ci FROM CartItem ci WHERE ci.id = :itemId")
  Optional<CartItem> findItemById(@Param("itemId") Long itemId);
}
