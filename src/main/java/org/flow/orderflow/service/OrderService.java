package org.flow.orderflow.service;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.cart.CartDto;
import org.flow.orderflow.dto.order.OrderDto;
import org.flow.orderflow.exception.NotFound;
import org.flow.orderflow.mapper.OrderMapper;
import org.flow.orderflow.model.Order;
import org.flow.orderflow.model.OrderItem;
import org.flow.orderflow.model.OrderStatus;
import org.flow.orderflow.repository.OrderRepository;
import org.flow.orderflow.repository.ProductRepository;
import org.flow.orderflow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;
  private final CartService cartService;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;

  public List<OrderDto> getAllOrders() {
    List<Order> orders = orderRepository.findAll();
    return orderMapper.toDtoList(orders);
  }

  public OrderDto getOrderById(Long id) {
    Order order = orderRepository.findById(id)
      .orElseThrow(() -> new NotFound("Order not found with id: " + id));
    return orderMapper.toDto(order);
  }

  public List<OrderDto> getOrdersByUserId(Long userId) {
    List<Order> orders = orderRepository.findByUserId(userId);
    if (orders.isEmpty()) {
      throw new NotFound("Orders not found for user with id: " + userId);
    }
    return orderMapper.toDtoList(orders);
  }

  public OrderDto createOrder(OrderDto orderDto) {
    CartDto cart = cartService.getCartByUserId(orderDto.getUserId());
    Order order = Order.builder()
      .user(userRepository.findById(orderDto.getUserId())
        .orElseThrow(() -> new NotFound("User not found with id: " + orderDto.getUserId())))
      .totalPrice(cart.getTotalPrice())
      .build();

    List<OrderItem> orderItems = cart.getItems().stream()
      .map(cartItem -> OrderItem.builder()
        .order(order)
        .product(productRepository.findById(cartItem.getProductId())
          .orElseThrow(() -> new NotFound("Product not found with id: " + cartItem.getProductId())))
        .quantity(cartItem.getQuantity())
        .price(cartItem.getPrice())
        .build())
      .collect(Collectors.toList());

    order.setItems(orderItems);
    Order savedOrder = orderRepository.save(order);
    cartService.clearCart(cart.getId());
    return orderMapper.toDto(savedOrder);
  }

  public OrderDto updateOrderStatus(Long id, OrderStatus newStatus) {
    Order order = orderRepository.findById(id)
      .orElseThrow(() -> new NotFound("Order not found with id: " + id));

    order.setStatus(newStatus);
    Order savedOrder = orderRepository.save(order);
    return orderMapper.toDto(savedOrder);
  }


  public void deleteOrder(Long id) {
    Order order = orderRepository.findById(id)
      .orElseThrow(() -> new NotFound("Order not found with id: " + id));
    orderRepository.delete(order);
  }
}
