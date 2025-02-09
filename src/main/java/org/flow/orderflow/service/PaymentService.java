package org.flow.orderflow.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.flow.orderflow.dto.order.OrderDto;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentService {
  public String cretePaymentIntent(OrderDto order) throws StripeException {

    long amount = Math.round(order.getTotalPrice() * 100);

    PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
      .setAmount(amount)
      .setCurrency("uah")
      .setAutomaticPaymentMethods(
        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
          .setEnabled(true)
          .build()
      )
      .putMetadata("order_id", order.getId().toString())
      .build();

    PaymentIntent paymentIntent = PaymentIntent.create(params);
    return paymentIntent.getClientSecret();
  }
}
