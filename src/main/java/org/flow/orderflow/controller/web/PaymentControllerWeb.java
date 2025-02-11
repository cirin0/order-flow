package org.flow.orderflow.controller.web;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.PaymentResponse;
import org.flow.orderflow.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentControllerWeb {

  private final PaymentService stripePaymentService;

  @PostMapping("/create")
  public String createPayment(@RequestParam Long orderId, Model model) {
    PaymentResponse paymentResponse = stripePaymentService.cretePaymentIntent(orderId);
    model.addAttribute("clientSecret", paymentResponse.getClientSecret());
    model.addAttribute("publishableKey", paymentResponse.getPublishableKey());
    model.addAttribute("amount", paymentResponse.getAmount());
    model.addAttribute("orderId", paymentResponse.getOrderId());
    model.addAttribute("currency", paymentResponse.getCurrency());
    return "payment/checkout";
  }

  @GetMapping("/success")
  public String handlePaymentSuccess(
    @RequestParam("payment_intent") String paymentIntent,
    RedirectAttributes redirectAttributes) {
    stripePaymentService.handlePaymentSuccess(paymentIntent);
    redirectAttributes.addFlashAttribute("success", "Оплата пройшла успішно!");
    return "redirect:/orders";
  }

  @GetMapping("/cancel")
  public String paymentCancel(RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("error", "Оплату скасовано");
    return "redirect:/orders";
  }
}
