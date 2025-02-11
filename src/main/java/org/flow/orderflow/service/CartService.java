package org.flow.orderflow.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.cart.CartDto;
import org.flow.orderflow.dto.cart.CartItemDto;
import org.flow.orderflow.mapper.CartMapper;
import org.flow.orderflow.model.Cart;
import org.flow.orderflow.model.CartItem;
import org.flow.orderflow.model.Product;
import org.flow.orderflow.model.User;
import org.flow.orderflow.repository.CartRepository;
import org.flow.orderflow.repository.ProductRepository;
import org.flow.orderflow.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {
  private final CartRepository cartRepository;
  private final CartMapper cartMapper;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;

  public void createCartForUser(User user) {
    Cart cart = Cart.builder()
      .user(user)
      .build();
    cartRepository.save(cart);
  }

  @Transactional
  public CartDto getOrCreateCartByUserId(Long userId) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new IllegalArgumentException("User not found"));

    Cart cart = cartRepository.findByUser(user)
      .orElseGet(() -> {
        Cart newCart = Cart.builder()
          .user(user)
          .build();
        return cartRepository.save(newCart);
      });

    return cartMapper.toDTO(cart);
  }

  @Transactional
  public CartDto getCartByUserId(Long userId) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено"));

    Cart cart = cartRepository.findByUser(user)
      .orElseThrow(() -> new IllegalArgumentException("Кошик не знайдено"));

    for (CartItem item : cart.getItems()) {
      Product product = item.getProduct();
      if (item.getQuantity() > product.getStock()) {
        item.setQuantity(product.getStock());
        cart.addWarningMessage("Товар '" + product.getName() + "' має недостатній запас. Доступно: " + product.getStock());
      }
    }

    cart.recalculateTotal();
    cartRepository.save(cart);

    return cartMapper.toDTO(cart);
  }

  @Transactional
  public CartDto addItemToCart(Long cartId, CartItemDto itemDto) {
    Cart cart = getCart(cartId);
    Product product = productRepository.findById(itemDto.getProductId())
      .orElseThrow(() -> new IllegalArgumentException("Product not found"));

    CartItem existingItem = findExistingItem(cart, product);
    if (existingItem != null) {
      existingItem.setQuantity(existingItem.getQuantity() + itemDto.getQuantity());
    } else {
      CartItem newItem = CartItem.builder()
        .cart(cart)
        .product(product)
        .quantity(itemDto.getQuantity())
        .build();
      cart.getItems().add(newItem);
    }

    cart.recalculateTotal();
    return cartMapper.toDTO(cartRepository.save(cart));
  }

  @Transactional
  public CartDto updateItemQuantity(Long cartId, Long itemId, Integer quantity) {
    Cart cart = getCart(cartId);
    CartItem item = cart.getItems().stream()
      .filter(i -> i.getId().equals(itemId))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

    Product product = item.getProduct();
    if (quantity > product.getStock()) {
      throw new IllegalArgumentException("Insufficient stock available");
    }

    item.setQuantity(quantity);
    cart.recalculateTotal();
    return cartMapper.toDTO(cartRepository.save(cart));
  }

  @Transactional
  public CartDto removeItemFromCart(Long cartId, Long itemId) {
    Cart cart = getCart(cartId);
    cart.getItems().removeIf(item -> item.getId().equals(itemId));
    cart.recalculateTotal();
    return cartMapper.toDTO(cartRepository.save(cart));
  }

  @Transactional
  public CartDto clearCart(Long cartId) {
    Cart cart = getCart(cartId);
    cart.getItems().clear();
    cart.setTotalPrice(0.0);
    return cartMapper.toDTO(cartRepository.save(cart));
  }

  private Cart getCart(Long cartId) {
    return cartRepository.findById(cartId)
      .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
  }

  private CartItem findExistingItem(Cart cart, Product product) {
    return cart.getItems().stream()
      .filter(item -> item.getProduct().getId().equals(product.getId()))
      .findFirst()
      .orElse(null);
  }

  public CartDto getCartByUserEmail(String email) {
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new IllegalArgumentException("User not found"));
    return cartMapper.toDTO(cartRepository.findByUser(user)
      .orElseThrow(() -> new IllegalArgumentException("Cart not found")));
  }


}
