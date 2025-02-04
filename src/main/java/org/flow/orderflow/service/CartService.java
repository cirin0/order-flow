package org.flow.orderflow.service;

import lombok.AllArgsConstructor;
import org.flow.orderflow.model.*;
import org.flow.orderflow.repository.CartRepository;
import org.flow.orderflow.repository.ProductRepository;
import org.flow.orderflow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CartService {
  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final OrderService orderService;

  @Transactional
  public Cart addToCart(Long userId, Long productId, int quantity) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new RuntimeException("User not found"));

    Product product = productRepository.findById(productId)
      .orElseThrow(() -> new RuntimeException("Product not found"));

    // Перевірка наявності товару
    if (product.getStock() < quantity) {
      throw new RuntimeException("Insufficient stock quantity");
    }

    // Отримання або створення кошика
    Cart cart = cartRepository.findByUserId(userId)
      .orElse(new Cart());
    cart.setUser(user);

    // Перевірка чи товар вже є в кошику
    CartItem existingItem = cart.getItems().stream()
      .filter(item -> item.getProduct().getId().equals(productId))
      .findFirst()
      .orElse(null);

    if (existingItem != null) {
      existingItem.setQuantity(existingItem.getQuantity() + quantity);
    } else {
      CartItem newItem = new CartItem();
      newItem.setCart(cart);
      newItem.setProduct(product);
      newItem.setQuantity(quantity);
      cart.getItems().add(newItem);
    }

    return cartRepository.save(cart);
  }

  public Cart getCart(Long userId) {
    return cartRepository.findByUserId(userId)
      .orElseThrow(() -> new RuntimeException("Cart not found"));
  }

  @Transactional
  public Cart removeFromCart(Long itemId) {
    CartItem item = cartRepository.findItemById(itemId)
      .orElseThrow(() -> new RuntimeException("Cart item not found"));

    Cart cart = item.getCart();
    cart.getItems().remove(item);
    return cartRepository.save(cart);
  }

  @Transactional
  public Order checkout(Long userId) {
    Cart cart = getCart(userId);
    if (cart.getItems().isEmpty()) {
      throw new RuntimeException("Cart is empty");
    }

    // Перевірка наявності всіх товарів
    for (CartItem item : cart.getItems()) {
      Product product = item.getProduct();
      if (product.getStock() < item.getQuantity()) {
        throw new RuntimeException(
          "Insufficient stock for product: " + product.getName());
      }
    }

    // Створення замовлення
    Order order = new Order();
    order.setUser(cart.getUser());

    // Перенесення товарів з кошика в замовлення
    for (CartItem cartItem : cart.getItems()) {
      OrderItem orderItem = new OrderItem();
      orderItem.setOrder(order);
      orderItem.setProduct(cartItem.getProduct());
      orderItem.setQuantity(cartItem.getQuantity());
      orderItem.setPrice(cartItem.getProduct().getPrice());

      // Оновлення складських залишків
      Product product = cartItem.getProduct();
      product.setStock(product.getStock() - cartItem.getQuantity());
      productRepository.save(product);

      order.getItems().add(orderItem);
    }

    // Очищення кошика
    cartRepository.delete(cart);

    // Збереження замовлення
    return orderService.saveOrder(order);
  }
}
