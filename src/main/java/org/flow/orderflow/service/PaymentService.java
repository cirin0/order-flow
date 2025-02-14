package org.flow.orderflow.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flow.orderflow.dto.PaymentResponse;
import org.flow.orderflow.exception.PaymentException;
import org.flow.orderflow.model.Order;
import org.flow.orderflow.model.OrderStatus;
import org.flow.orderflow.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

  @Value("${stripe.secret.key}")
  private String stripeSecretKey;

  @Value("${stripe.publishable.key}")
  private String stripePublishableKey;

  private final OrderRepository orderRepository;

  @PostConstruct
  public void init() {
    Stripe.apiKey = stripeSecretKey;
  }

  @Transactional
  public PaymentResponse cretePaymentIntent(Long orderId) {
    try {
      Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new PaymentException("Order not found"));

      if (order.getStatus() != OrderStatus.NEW) {
        throw new PaymentException("Invalid order status for payment");
      }
      PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
        .setAmount(convertToStripeAmount(order.getTotalPrice()))
        .setCurrency("uah")
        .setDescription("Оплата замовлення: #" + order.getId())
        .putMetadata("orderId", order.getId().toString())
        .setAutomaticPaymentMethods(
          PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
            .setEnabled(true)
            .build()
        )
        .build();

      PaymentIntent paymentIntent = PaymentIntent.create(createParams);

      return PaymentResponse.builder()
        .clientSecret(paymentIntent.getClientSecret())
        .publishableKey(stripePublishableKey)
        .amount(order.getTotalPrice())
        .currency("uah")
        .orderId(orderId)
        .build();
    } catch (StripeException e) {
      log.error("Failed to create Stripe payment session", e);
      throw new PaymentException("Payment initialization failed: " + e.getMessage());
    }
  }

  @Transactional
  public void handlePaymentSuccess(String paymentIntentId) {
    try {
      PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
      String orderId = paymentIntent.getMetadata().get("orderId");

      if (orderId != null) {
        Order order = orderRepository.findById(Long.parseLong(orderId))
          .orElseThrow(() -> new PaymentException("Order not found"));

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        log.info("Payment successful for order: {}", orderId);
      }
    } catch (StripeException e) {
      log.error("Failed to handle successful payment", e);
      throw new PaymentException("Failed to process successful payment");
    }
  }

  private Long convertToStripeAmount(Double amount) {
    return Math.round(amount * 100);
  }
}
