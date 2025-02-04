package org.flow.orderflow.controller;

import lombok.AllArgsConstructor;
import org.flow.orderflow.model.Cart;
import org.flow.orderflow.model.Order;
import org.flow.orderflow.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@AllArgsConstructor
public class CartController {
  private final CartService cartService;

  @PostMapping("/add/{productId}")
  public ResponseEntity<Cart> addToCart(@PathVariable Long productId,
                                        @RequestParam int quantity,
                                        @RequestParam Long userId) {
    return ResponseEntity.ok(cartService.addToCart(userId, productId, quantity));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<Cart> getCart(@PathVariable Long userId) {
    return ResponseEntity.ok(cartService.getCart(userId));
  }

  @DeleteMapping("/item/{itemId}")
  public ResponseEntity<Cart> removeFromCart(@PathVariable Long itemId) {
    return ResponseEntity.ok(cartService.removeFromCart(itemId));
  }

  @PostMapping("/checkout/{userId}")
  public ResponseEntity<Order> checkout(@PathVariable Long userId) {
    return ResponseEntity.ok(cartService.checkout(userId));
  }
}
