package org.flow.orderflow.controller.web;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.order.OrderDto;
import org.flow.orderflow.dto.user.UserSessionDto;
import org.flow.orderflow.model.OrderStatus;
import org.flow.orderflow.service.OrderService;
import org.flow.orderflow.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;
  private final OrderService orderService;

  @Value("${stripe.public.key}")
  String publicKey;

  @GetMapping("/process/{orderId}")
  public String processPayment(@PathVariable Long orderId, Model model, HttpSession session) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    if (user == null) {
      return "redirect:auth/login";
    }
    try {
      OrderDto order = orderService.getOrderById(orderId);
      String clientSecret = paymentService.cretePaymentIntent(order);

      model.addAttribute("clientSecret", clientSecret);
      model.addAttribute("order", order);
      model.addAttribute("publicKey", publicKey);

      return "payment/checkout";
    } catch (StripeException e) {
      model.addAttribute("error", "Помилка при створенні платежу" + e.getMessage());
      return "redirect:/orders/" + orderId;
    }
  }

  @PostMapping("/webhook")
  @ResponseBody
  public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                              @RequestHeader("Stripe-Signature") String sigHeader) {
    try {
      Event event = Event.retrieve(payload);

      if ("payment_intent.succeeded".equals(event.getType())) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
        String orderId = paymentIntent.getMetadata().get("orderId");

        // Оновлюємо статус замовлення
        orderService.updateOrderStatus(Long.parseLong(orderId), OrderStatus.PROCESSING);
      }

      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Webhook error: " + e.getMessage());
    }
  }
}
