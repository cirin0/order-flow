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

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatusScheduler {
  private final OrderRepository orderRepository;

  @Scheduled(fixedRate = 120000)
  @Transactional
  public void processOrders() {
    LocalDateTime time = LocalDateTime.now().minusSeconds(10);

    List<Order> newOrders = orderRepository.findByStatusAndOrderDateBefore(
      OrderStatus.NEW,
      time
    );

    for (Order order : newOrders) {
      order.setStatus(OrderStatus.TESTING);
      orderRepository.save(order);
      log.info("Order {} status automatically changed from NEW to PROCESSING", order.getId());
    }
  }
}
