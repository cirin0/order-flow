package org.flow.orderflow.controller.api;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.order.OrderDto;
import org.flow.orderflow.model.OrderStatus;
import org.flow.orderflow.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
  private final OrderService orderService;

  @GetMapping
  public ResponseEntity<List<OrderDto>> getAllOrders() {
    List<OrderDto> orders = orderService.getAllOrders();
    return ResponseEntity.ok(orders);
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
    OrderDto order = orderService.getOrderById(id);
    return ResponseEntity.ok(order);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<OrderDto>> getOrdersByUserId(@PathVariable Long userId) {
    List<OrderDto> orders = orderService.getOrdersByUserId(userId);
    return ResponseEntity.ok(orders);
  }

  @GetMapping("/user/{userEmail}")
  public ResponseEntity<OrderDto> getOrderByUserEmail(@PathVariable String userEmail) {
    OrderDto order = orderService.getOrderByUserEmail(userEmail);
    return ResponseEntity.ok(order);
  }

  @PostMapping
  public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
    String userEmail = "ivan.gruziv@gmail.com";
    OrderDto order = orderService.createOrder(orderDto, userEmail);
    return ResponseEntity.status(HttpStatus.CREATED).body(order);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
    orderService.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
    OrderDto updatedOrder = orderService.updateOrderStatus(id, status);
    return ResponseEntity.ok(updatedOrder);
  }

  @PatchMapping("/{id}/cancel")
  public ResponseEntity<OrderDto> cancelOrder(@PathVariable Long id) {
    OrderDto updatedOrder = orderService.cancelOrder(id);
    return ResponseEntity.ok(updatedOrder);
  }

  @PatchMapping("/{id}/complete")
  public ResponseEntity<OrderDto> completeOrder(@PathVariable Long id) {
    OrderDto updatedOrder = orderService.completeOrder(id);
    return ResponseEntity.ok(updatedOrder);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(e.getMessage());
  }
}
