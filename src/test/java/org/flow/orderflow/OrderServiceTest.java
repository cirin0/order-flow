package org.flow.orderflow;

import org.flow.orderflow.dto.cart.CartDto;
import org.flow.orderflow.dto.cart.CartItemDto;
import org.flow.orderflow.dto.order.DeliveryAddressDto;
import org.flow.orderflow.dto.order.OrderDto;
import org.flow.orderflow.exception.NotFound;
import org.flow.orderflow.mapper.OrderMapper;
import org.flow.orderflow.model.*;
import org.flow.orderflow.repository.OrderRepository;
import org.flow.orderflow.repository.ProductRepository;
import org.flow.orderflow.repository.UserRepository;
import org.flow.orderflow.service.CartService;
import org.flow.orderflow.service.MailSenderService;
import org.flow.orderflow.service.OrderService;
import org.flow.orderflow.service.PdfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private OrderMapper orderMapper;

  @Mock
  private CartService cartService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private MailSenderService mailSenderService;

  @Mock
  private PdfService pdfService;

  @InjectMocks
  private OrderService orderService;

  private Order order;
  private OrderDto orderDto;
  private User user;
  private Product product;
  private CartDto cartDto;

  @BeforeEach
  void setUp() {
    user = User.builder()
      .id(1L)
      .email("test@example.com")
      .first_name("Test")
      .last_name("User")
      .build();

    product = Product.builder()
      .id(1L)
      .name("Test Product")
      .price(100.0)
      .stock(10)
      .build();

    DeliveryAddress deliveryAddress = DeliveryAddress.builder()
      .id(1L)
      .region("Test Region")
      .city("Test City")
      .area("Test Area")
      .street("Test Street")
      .house("1")
      .apartment("1")
      .build();

    DeliveryAddressDto deliveryAddressDto = DeliveryAddressDto.builder()
      .id(1L)
      .region("Test Region")
      .city("Test City")
      .area("Test Area")
      .street("Test Street")
      .house("1")
      .apartment("1")
      .build();

    order = Order.builder()
      .id(1L)
      .orderNumber("1234567890")
      .user(user)
      .totalPrice(100.0)
      .status(OrderStatus.NEW)
      .deliveryAddress(deliveryAddress)
      .build();

    List<OrderItem> orderItems = new ArrayList<>();
    OrderItem orderItem = OrderItem.builder()
      .id(1L)
      .order(order)
      .product(product)
      .quantity(1)
      .price(100.0)
      .build();
    orderItems.add(orderItem);
    order.setItems(orderItems);

    orderDto = OrderDto.builder()
      .id(1L)
      .orderNumber("1234567890")
      .userId(1L)
      .totalPrice(100.0)
      .status(OrderStatus.NEW)
      .deliveryAddress(deliveryAddressDto)
      .build();

    List<CartItemDto> cartItems = new ArrayList<>();
    CartItemDto cartItemDto = CartItemDto.builder()
      .id(1L)
      .productId(1L)
      .quantity(1)
      .price(100.0)
      .build();
    cartItems.add(cartItemDto);

    cartDto = CartDto.builder()
      .id(1L)
      .userId(1L)
      .items(cartItems)
      .totalPrice(100.0)
      .build();
  }

  @Test
  void getAllOrders_shouldReturnAllOrders() {
    List<Order> orders = Collections.singletonList(order);
    List<OrderDto> expectedDtos = Collections.singletonList(orderDto);

    when(orderRepository.findAll()).thenReturn(orders);
    when(orderMapper.toDtoList(orders)).thenReturn(expectedDtos);

    List<OrderDto> result = orderService.getAllOrders();

    assertThat(result).isEqualTo(expectedDtos);
    verify(orderRepository).findAll();
    verify(orderMapper).toDtoList(orders);
  }

  @Test
  void getOrderById_whenOrderExists_shouldReturnOrder() {
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderMapper.toDto(order)).thenReturn(orderDto);

    OrderDto result = orderService.getOrderById(1L);

    assertThat(result).isEqualTo(orderDto);
    verify(orderRepository).findById(1L);
    verify(orderMapper).toDto(order);
  }

  @Test
  void getOrderById_whenOrderDoesNotExist_shouldThrowNotFound() {
    when(orderRepository.findById(999L)).thenReturn(Optional.empty());

    NotFound exception = assertThrows(NotFound.class, () ->
      orderService.getOrderById(999L)
    );

    assertThat(exception.getMessage()).contains("Order not found with id: 999");
    verify(orderRepository).findById(999L);
  }

  @Test
  void getOrdersByUserId_shouldReturnUserOrders() {
    List<Order> orders = Collections.singletonList(order);
    List<OrderDto> expectedDtos = Collections.singletonList(orderDto);

    when(orderRepository.findByUserId(1L)).thenReturn(orders);
    when(orderMapper.toDtoList(orders)).thenReturn(expectedDtos);

    List<OrderDto> result = orderService.getOrdersByUserId(1L);

    assertThat(result).isEqualTo(expectedDtos);
    verify(orderRepository).findByUserId(1L);
    verify(orderMapper).toDtoList(orders);
  }

  @Test
  void getAllOrdersWithUserDetails_shouldReturnAllOrdersWithUserDetails() {
    List<Order> orders = Collections.singletonList(order);
    List<OrderDto> expectedDtos = Collections.singletonList(orderDto);

    when(orderRepository.findAll()).thenReturn(orders);
    when(orderMapper.toDtoList(orders)).thenReturn(expectedDtos);

    List<OrderDto> result = orderService.getAllOrdersWithUserDetails();

    assertThat(result).isEqualTo(expectedDtos);
    verify(orderRepository).findAll();
    verify(orderMapper).toDtoList(orders);
  }

  @Test
  void getOrderByUserEmail_whenOrderExists_shouldReturnOrder() {
    List<Order> orders = Collections.singletonList(order);
    List<OrderDto> expectedDtos = Collections.singletonList(orderDto);
    String email = "test@example.com";
    when(orderRepository.findByUserEmail(email)).thenReturn(orders);
    when(orderMapper.toDtoList(orders)).thenReturn(expectedDtos);

    List<OrderDto> result = orderService.getOrderByUserEmail(email);

    assertThat(result).isEqualTo(orderDto);
    verify(orderRepository).findByUserEmail(email);
    verify(orderMapper).toDto(order);
  }

  @Test
  void getOrderByUserEmail_whenOrderDoesNotExist_shouldThrowNotFound() {
    List<Order> orders = Collections.singletonList(order);
    List<OrderDto> expectedDtos = Collections.singletonList(orderDto);
    String email = "nonexistent@example.com";

    when(orderRepository.findByUserEmail(email)).thenReturn(orders);
    when(orderMapper.toDtoList(orders)).thenReturn(expectedDtos);

    List<OrderDto> result = orderService.getOrderByUserEmail(email);

    assertThat(result).isEqualTo(expectedDtos);
    verify(orderRepository).findAll();
    verify(orderMapper).toDtoList(orders);
  }

  @Test
  void updateOrderToPaid_whenOrderExistsAndIsNew_shouldUpdateStatusToPaid() {
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderRepository.save(order)).thenReturn(order);
    when(orderMapper.toDto(order)).thenReturn(orderDto);

    OrderDto result = orderService.updateOrderToPaid(1L);

    assertThat(result).isEqualTo(orderDto);
    assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    verify(orderRepository).findById(1L);
    verify(orderRepository).save(order);
    verify(orderMapper).toDto(order);
  }

  @Test
  void updateOrderToPaid_whenOrderIsNotNew_shouldThrowIllegalStateException() {
    order.setStatus(OrderStatus.PAID);
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
      orderService.updateOrderToPaid(1L)
    );

    assertThat(exception.getMessage()).contains("Можна оплатити тільки нове замовлення");
    verify(orderRepository).findById(1L);
    verifyNoMoreInteractions(orderRepository);
  }

  @Test
  void createOrder_withValidData_shouldCreateOrder() {
    String userEmail = "test@example.com";
    CartDto returnedCartDto = CartDto.builder().id(1L).build();

    when(cartService.getCartByUserId(1L)).thenReturn(cartDto);
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(orderMapper.toDto(order)).thenReturn(orderDto);
    when(orderRepository.existsByOrderNumber(anyString())).thenReturn(false);
    when(pdfService.generateInvoice(orderDto)).thenReturn(new byte[0]);
    doNothing().when(mailSenderService).sendOrderConfirmationMail(anyString(), any(OrderDto.class));
    when(cartService.clearCart(anyLong())).thenReturn(returnedCartDto);

    OrderDto result = orderService.createOrder(orderDto, userEmail);

    assertThat(result).isEqualTo(orderDto);
    verify(cartService).getCartByUserId(1L);
    verify(productRepository, times(2)).findById(1L);
    verify(userRepository).findById(1L);
    verify(orderRepository).save(any(Order.class));
    verify(pdfService).generateInvoice(orderDto);
    verify(orderMapper, times(3)).toDto(order);
    verify(cartService).clearCart(1L);
  }

  @Test
  void createOrder_withInsufficientStock_shouldThrowIllegalStateException() {
    String userEmail = "test@example.com";
    product.setStock(0); // недостатньо на складі
    when(cartService.getCartByUserId(1L)).thenReturn(cartDto);
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
      orderService.createOrder(orderDto, userEmail)
    );

    assertThat(exception.getMessage()).contains("Insufficient stock for product");
    verify(cartService).getCartByUserId(1L);
    verify(productRepository).findById(1L);
    verifyNoMoreInteractions(orderRepository, orderMapper);
  }

  @Test
  void updateOrderStatus_withValidStatusChange_shouldUpdateStatus() {
    OrderStatus newStatus = OrderStatus.PROCESSING;
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderRepository.save(order)).thenReturn(order);
    orderDto.setStatus(newStatus);
    when(orderMapper.toDto(order)).thenReturn(orderDto);

    OrderDto result = orderService.updateOrderStatus(1L, newStatus);

    assertThat(result.getStatus()).isEqualTo(newStatus);
    verify(orderRepository).findById(1L);
    verify(orderRepository).save(order);
    verify(orderMapper).toDto(order);
  }

  @Test
  void updateOrderStatus_whenOrderIsCompleted_shouldThrowIllegalStateException() {
    order.setStatus(OrderStatus.COMPLETED);
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
      orderService.updateOrderStatus(1L, OrderStatus.PROCESSING)
    );

    assertThat(exception.getMessage()).contains("Не можна змінити статус скасованого або завершеного замовлення");
    verify(orderRepository).findById(1L);
    verifyNoMoreInteractions(orderRepository);
  }

  @Test
  void cancelOrder_whenOrderIsNew_shouldCancelOrderAndRestoreStock() {
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderRepository.save(order)).thenReturn(order);
    orderDto.setStatus(OrderStatus.CANCELED);
    when(orderMapper.toDto(order)).thenReturn(orderDto);

    OrderDto result = orderService.cancelOrder(1L);

    assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELED);
    assertThat(product.getStock()).isEqualTo(11); // повернути товар на склад
    verify(orderRepository).findById(1L);
    verify(productRepository).save(product);
    verify(orderRepository).save(order);
    verify(orderMapper).toDto(order);
  }

  @Test
  void cancelOrder_whenOrderIsDelivered_shouldThrowIllegalStateException() {
    order.setStatus(OrderStatus.DELIVERED);
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
      orderService.cancelOrder(1L)
    );

    assertThat(exception.getMessage()).contains("Cannot cancel order with status");
    verify(orderRepository).findById(1L);
    verifyNoMoreInteractions(orderRepository, productRepository);
  }

  @Test
  void completeOrder_whenOrderIsDelivered_shouldCompleteOrder() {
    order.setStatus(OrderStatus.DELIVERED);
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderRepository.save(order)).thenReturn(order);
    orderDto.setStatus(OrderStatus.COMPLETED);
    when(orderMapper.toDto(order)).thenReturn(orderDto);

    OrderDto result = orderService.completeOrder(1L);

    assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    verify(orderRepository).findById(1L);
    verify(orderRepository).save(order);
    verify(orderMapper).toDto(order);
  }

  @Test
  void completeOrder_whenOrderIsNotDelivered_shouldThrowIllegalStateException() {
    order.setStatus(OrderStatus.PROCESSING);
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
      orderService.completeOrder(1L)
    );

    assertThat(exception.getMessage()).contains("Cannot complete order with status");
    verify(orderRepository).findById(1L);
    verifyNoMoreInteractions(orderRepository);
  }

  @Test
  void deleteOrder_whenOrderExists_shouldDeleteOrder() {
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    doNothing().when(orderRepository).delete(order);

    orderService.deleteOrder(1L);

    verify(orderRepository).findById(1L);
    verify(orderRepository).delete(order);
  }

  @Test
  void deleteOrder_whenOrderDoesNotExist_shouldThrowNotFound() {
    when(orderRepository.findById(999L)).thenReturn(Optional.empty());

    NotFound exception = assertThrows(NotFound.class, () ->
      orderService.deleteOrder(999L)
    );

    assertThat(exception.getMessage()).contains("Order not found with id: 999");
    verify(orderRepository).findById(999L);
    verifyNoMoreInteractions(orderRepository);
  }
}
