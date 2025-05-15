package org.flow.orderflow.controller.api;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.cart.CartDto;
import org.flow.orderflow.dto.cart.CartItemDto;
import org.flow.orderflow.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {
  private final CartService cartService;

  @GetMapping("/{userId}")
  public ResponseEntity<CartDto> getCartByUserId(@PathVariable Long userId) {
    return ResponseEntity.ok(cartService.getCartByUserId(userId));
  }

  @PostMapping("/{cartId}/items")
  public ResponseEntity<CartDto> addItem(
    @PathVariable Long cartId,
    @RequestBody CartItemDto itemDto) {
    return ResponseEntity.ok(cartService.addItemToCart(cartId, itemDto));
  }

  @PutMapping("/{cartId}/items/{itemId}")
  public ResponseEntity<CartDto> updateItemQuantity(
    @PathVariable Long cartId,
    @PathVariable Long itemId,
    @RequestParam Integer quantity) {
    return ResponseEntity.ok(cartService.updateItemQuantity(cartId, itemId, quantity));
  }

  @DeleteMapping("/{cartId}/items/{itemId}")
  public ResponseEntity<CartDto> removeItem(
    @PathVariable Long cartId,
    @PathVariable Long itemId) {
    return ResponseEntity.ok(cartService.removeItemFromCart(cartId, itemId));
  }

  @DeleteMapping("/{cartId}/clear")
  public ResponseEntity<CartDto> clearCart(@PathVariable Long cartId) {
    return ResponseEntity.ok(cartService.clearCart(cartId));
  }
}
