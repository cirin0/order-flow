package org.flow.orderflow.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flow.orderflow.model.Order;
import org.flow.orderflow.model.OrderStatus;
import org.flow.orderflow.repository.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatusScheduler {
  private final OrderRepository orderRepository;

  @Scheduled(timeUnit = SECONDS, fixedRate = 300)
  @Transactional
  public void processOrders() {
    LocalDateTime time = LocalDateTime.now().minusMinutes(5);
    log.info("Checking for orders from status NEW to PROCESSING");
    List<Order> newOrders = orderRepository.findByStatusAndOrderDateBefore(
      OrderStatus.NEW,
      time
    );

    for (Order order : newOrders) {
      order.setStatus(OrderStatus.PROCESSING);
      orderRepository.save(order);
      log.info("Order {} status automatically changed from NEW to PROCESSING", order.getId());
    }
  }

  @Scheduled(timeUnit = SECONDS, fixedRate = 300)
  @Transactional
  public void cancelOrders() {
    LocalDateTime time = LocalDateTime.now().minusMinutes(10);
    log.info("Checking for orders from status DELIVERED to CANCELLED");
    List<Order> processingOrders = orderRepository.findByStatusAndOrderDateBefore(
      OrderStatus.DELIVERED,
      time
    );

    for (Order order : processingOrders) {
      order.setStatus(OrderStatus.CANCELED);
      orderRepository.save(order);
      log.info("Order {} status automatically changed from DELIVERED to CANCELLED", order.getId());
    }
  }

  @Scheduled(timeUnit = SECONDS, fixedRate = 60)
  @Transactional
  public void checkPaidOrders() {
    LocalDateTime time = LocalDateTime.now().minusMinutes(10);
    log.info("Checking for orders from status PAID to SHIPPED");
    List<Order> processingOrders = orderRepository.findByStatusAndOrderDateBefore(
      OrderStatus.PAID,
      time
    );

    for (Order order : processingOrders) {
      order.setStatus(OrderStatus.SHIPPED);
      orderRepository.save(order);
      log.info("Order {} status automatically changed from PAID to SHIPPED", order.getId());
    }
  }
}
