package org.flow.orderflow.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flow.orderflow.dto.cart.CartDto;
import org.flow.orderflow.dto.order.OrderDto;
import org.flow.orderflow.exception.NotFound;
import org.flow.orderflow.mapper.OrderMapper;
import org.flow.orderflow.model.Order;
import org.flow.orderflow.model.OrderItem;
import org.flow.orderflow.model.OrderStatus;
import org.flow.orderflow.model.Product;
import org.flow.orderflow.repository.OrderRepository;
import org.flow.orderflow.repository.ProductRepository;
import org.flow.orderflow.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;
  private final CartService cartService;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final MailSenderService mailSenderService;

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

  public OrderDto getOrderByUserEmail(String email) {
    Order order = orderRepository.findByUserEmail(email)
      .orElseThrow(() -> new NotFound("Order not found for user with email: " + email));
    return orderMapper.toDto(order);
  }

  @Transactional
  public OrderDto createOrder(OrderDto orderDto, String userEmail) {
    CartDto cart = cartService.getCartByUserId(orderDto.getUserId());

    for (var cartItem : cart.getItems()) {
      Product product = productRepository.findById(cartItem.getProductId())
        .orElseThrow(() -> new NotFound("Product not found with id: " + cartItem.getProductId()));

      if (product.getStock() < cartItem.getQuantity()) {
        throw new IllegalStateException(
          String.format("Insufficient stock for product %s. Available: %d, Requested: %d",
            product.getName(), product.getStock(), cartItem.getQuantity())
        );
      }
    }

    Order order = Order.builder()
      .user(userRepository.findById(orderDto.getUserId())
        .orElseThrow(() -> new NotFound("User not found with id: " + orderDto.getUserId())))
      .totalPrice(cart.getTotalPrice())
      .build();

//    List<OrderItem> orderItems = cart.getItems().stream()
//      .map(cartItem -> OrderItem.builder()
//        .order(order)
//        .product(productRepository.findById(cartItem.getProductId())
//          .orElseThrow(() -> new NotFound("Product not found with id: " + cartItem.getProductId())))
//        .quantity(cartItem.getQuantity())
//        .price(cartItem.getPrice())
//        .build())
//      .collect(Collectors.toList());

    List<OrderItem> orderItems = cart.getItems().stream()
      .map(cartItem -> {
        Product product = productRepository.findById(cartItem.getProductId())
          .orElseThrow(() -> new NotFound("Product not found with id: " + cartItem.getProductId()));
        product.setStock(product.getStock() - cartItem.getQuantity());
        productRepository.save(product);
        return OrderItem.builder()
          .order(order)
          .product(product)
          .quantity(cartItem.getQuantity())
          .price(cartItem.getPrice())
          .build();
      })
      .collect(Collectors.toList());

    order.setItems(orderItems);
    Order savedOrder = orderRepository.save(order);
    sendOrderConfirmationEmail(orderMapper.toDto(savedOrder), userEmail);
//    cartService.clearCart(cart.getId());
    return orderMapper.toDto(savedOrder);
  }

  public OrderDto updateOrderStatus(Long id, OrderStatus newStatus) {
    Order order = orderRepository.findById(id)
      .orElseThrow(() -> new NotFound("Order not found with id: " + id));
    if (order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.COMPLETED) {
      throw new IllegalStateException("Cannot change status of canceled or completed order");
    }
    order.setStatus(newStatus);
    Order savedOrder = orderRepository.save(order);
    return orderMapper.toDto(savedOrder);
  }

  @Transactional
  public OrderDto cancelOrder(Long id) {
    Order order = orderRepository.findById(id)
      .orElseThrow(() -> new NotFound("Order not found with id: " + id));

    if (order.getStatus() != OrderStatus.NEW && order.getStatus() != OrderStatus.PROCESSING) {
      throw new IllegalStateException(
        String.format("Cannot cancel order with status %s. Only NEW or PROCESSING orders can be canceled",
          order.getStatus())
      );
    }

    for (OrderItem item : order.getItems()) {
      Product product = item.getProduct();
      product.setStock(product.getStock() + item.getQuantity());
      productRepository.save(product);
    }
    order.setStatus(OrderStatus.CANCELED);
    Order savedOrder = orderRepository.save(order);
    return orderMapper.toDto(savedOrder);
  }

  @Transactional
  public OrderDto completeOrder(Long id) {
    Order order = orderRepository.findById(id)
      .orElseThrow(() -> new NotFound("Order not found with id: " + id));
    if (order.getStatus() != OrderStatus.DELIVERED) {
      throw new IllegalStateException(
        String.format("Cannot complete order with status %s. Only DELIVERED orders can be completed",
          order.getStatus())
      );
    }
    order.setStatus(OrderStatus.COMPLETED);
    Order savedOrder = orderRepository.save(order);
    return orderMapper.toDto(savedOrder);
  }

  public void deleteOrder(Long id) {
    Order order = orderRepository.findById(id)
      .orElseThrow(() -> new NotFound("Order not found with id: " + id));
    orderRepository.delete(order);
  }

  @Async
  public void sendOrderConfirmationEmail(OrderDto orderDto, String userEmail) {
    try {
//      String email = "ivan.gruziv@gmail.com";
      String orderDetails = createOrderDetailsMessage(orderDto);
      mailSenderService.sendOrderConfirmationMail(userEmail, orderDetails);
      log.info("Order confirmation email sent to {}", userEmail);
    } catch (Exception e) {
      log.error("Failed to send order confirmation email", e);
    }
  }

  private String createOrderDetailsMessage(OrderDto orderDto) {
    // Створення тексту листа
    StringBuilder message = new StringBuilder();
    message.append("Thank you for your order!\n\n");
    message.append("Order Details:\n");
    message.append("Order ID: ").append(orderDto.getId()).append("\n");
    message.append("Total Price: ").append(orderDto.getTotalPrice()).append("\n\n");
    message.append("Items:\n");

    orderDto.getItems().forEach(item ->
      message.append("- ")
        .append(item.getProductName())
        .append(" x ")
        .append(item.getQuantity())
        .append(" = ")
        .append(item.getPrice())
        .append("\n")
    );

    return message.toString();
  }
}
