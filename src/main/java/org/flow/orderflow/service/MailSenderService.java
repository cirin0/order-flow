package org.flow.orderflow.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flow.orderflow.dto.order.OrderDto;
import org.flow.orderflow.dto.order.OrderItemDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderService {

  @Value("${spring.mail.username}")
  private String from;
  private final JavaMailSender mailSender;

  @Async
  public void sendMail(String to, String subject, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
    message.setFrom(from);
    mailSender.send(message);
  }

  @Async
  public void sendOrderConfirmationMail(String to, OrderDto order) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(from);
      helper.setTo(to);
      helper.setSubject("Підтвердження замовлення #" + order.getId());
      helper.setText(createOrderConfirmationHtml(order), true);

      mailSender.send(message);
      log.info("Order confirmation email sent successfully to: {}", to);
    } catch (MessagingException e) {
      log.error("Failed to send order confirmation email to: {}", to, e);
      throw new RuntimeException("Failed to send email", e);
    }
  }

  private String createOrderConfirmationHtml(OrderDto order) {
    StringBuilder html = new StringBuilder();
    html.append("<!DOCTYPE html>")
      .append("<html>")
      .append("<head>")
      .append("<style>")
      .append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }")
      .append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }")
      .append(".header { background-color: #f8f9fa; padding: 20px; text-align: center; }")
      .append(".order-details { margin: 20px 0; padding: 20px; border: 1px solid #ddd; }")
      .append(".items-table { width: 100%; border-collapse: collapse; margin: 20px 0; }")
      .append(".items-table th, .items-table td { padding: 10px; border: 1px solid #ddd; text-align: left; }")
      .append(".items-table th { background-color: #f8f9fa; }")
      .append(".total { text-align: right; font-weight: bold; margin-top: 20px; }")
      .append("</style>")
      .append("</head>")
      .append("<body>")
      .append("<div class='container'>")
      .append("<div class='header'>")
      .append("<h1>Thank you for your order!</h1>")
      .append("<p>We're processing your order and will keep you updated on its status.</p>")
      .append("</div>")
      .append("<div class='order-details'>")
      .append("<h2>Order Details</h2>")
      .append("<p>Order ID: ").append(order.getId()).append("</p>");

    if (order.getOrderDate() != null) {
      html.append("<p>Order Date: ")
        .append(order.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
        .append("</p>");
    }

    html.append("<table class='items-table'>")
      .append("<thead>")
      .append("<tr>")
      .append("<th>Product</th>")
      .append("<th>Quantity</th>")
      .append("<th>Price</th>")
      .append("<th>Total</th>")
      .append("</tr>")
      .append("</thead>")
      .append("<tbody>");

    for (OrderItemDto item : order.getItems()) {
      html.append("<tr>")
        .append("<td>").append(item.getProductName()).append("</td>")
        .append("<td>").append(item.getQuantity()).append("</td>")
        .append("<td>").append(String.format("%.2f UAH", item.getPrice())).append("</td>")
        .append("<td>").append(String.format("%.2f UAH", item.getPrice() * item.getQuantity())).append("</td>")
        .append("</tr>");
    }

    html.append("</tbody>")
      .append("</table>")
      .append("<div class='total'>")
      .append("<p>Total Amount: ").append(String.format("%.2f UAH", order.getTotalPrice())).append("</p>")
      .append("</div>")
      .append("</div>")
      .append("<div style='text-align: center; margin-top: 30px; color: #666;'>")
      .append("<p>If you have any questions about your order, please contact our customer support.</p>")
      .append("<p>Thank you for shopping with us!</p>")
      .append("</div>")
      .append("</div>")
      .append("</body>")
      .append("</html>");

    return html.toString();
  }
}
