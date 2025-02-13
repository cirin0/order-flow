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
      .append("body { font-family: 'Arial', sans-serif; line-height: 1.6; color: #333; background-color: #ffffff; margin: 0; padding: 0; }")
      .append(".container { max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); border: 1px solid #e0e0e0; }")
      .append(".header { background-color: #000000; color: #ffffff; padding: 30px 20px; text-align: center; border-radius: 10px 10px 0 0; position: relative; overflow: hidden; }")
      .append(".header h1 { margin: 0; font-size: 28px; font-weight: bold; animation: fadeIn 1.5s ease-in-out; }")
      .append(".header p { margin: 10px 0 0; font-size: 16px; color: #cccccc; animation: fadeIn 2s ease-in-out; }")
      .append(".header::before { content: ''; position: absolute; top: 0; left: -100%; width: 100%; height: 100%; background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent); animation: shine 3s infinite; }")
      .append(".order-details { margin: 20px 0; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #f9f9f9; position: relative; }")
      .append(".order-details h2 { margin-top: 0; font-size: 24px; color: #000000; position: relative; display: inline-block; }")
      .append(".order-details h2::after { content: ''; position: absolute; bottom: -5px; left: 0; width: 100%; height: 2px; background: linear-gradient(90deg, #000000, #ffffff); }")
      .append(".order-details p { margin: 10px 0; font-size: 16px; color: #555555; }")
      .append(".items-table { width: 100%; border-collapse: collapse; margin: 20px 0; }")
      .append(".items-table th, .items-table td { padding: 12px; border: 1px solid #e0e0e0; text-align: left; }")
      .append(".items-table th { background-color: #000000; color: #ffffff; font-weight: bold; }")
      .append(".items-table td { background-color: #ffffff; transition: background-color 0.3s ease; }")
      .append(".items-table tr:hover td { background-color: #f0f0f0; }")
      .append(".items-table tr:nth-child(even) td { background-color: #f9f9f9; }")
      .append(".total { text-align: right; font-weight: bold; margin-top: 20px; font-size: 18px; color: #000000; }")
      .append(".footer { text-align: center; margin-top: 30px; color: #666666; font-size: 14px; }")
      .append(".footer p { margin: 5px 0; }")
      .append("@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }")
      .append("@keyframes shine { 0% { left: -100%; } 100% { left: 100%; } }")
      .append("</style>")
      .append("</head>")
      .append("<body>")
      .append("<div class='container'>")
      .append("<div class='header'>")
      .append("<h1>Дякуємо за ваше замовлення!</h1>")
      .append("<p>Ми обробляємо ваше замовлення та повідомимо вас про його статус.</p>")
      .append("</div>")
      .append("<div class='order-details'>")
      .append("<h2>Деталі замовлення</h2>")
      .append("<p>Номер замовлення: ").append(order.getId()).append("</p>");

    if (order.getOrderDate() != null) {
      html.append("<p>Дата замовлення: ")
        .append(order.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
        .append("</p>");
    }

    html.append("<table class='items-table'>")
      .append("<thead>")
      .append("<tr>")
      .append("<th>Товар</th>")
      .append("<th>Кількість</th>")
      .append("<th>Ціна</th>")
      .append("<th>Сума</th>")
      .append("</tr>")
      .append("</thead>")
      .append("<tbody>");

    for (OrderItemDto item : order.getItems()) {
      html.append("<tr>")
        .append("<td>").append(item.getProductName()).append("</td>")
        .append("<td>").append(item.getQuantity()).append("</td>")
        .append("<td>").append(String.format("%.2f грн", item.getPrice())).append("</td>")
        .append("<td>").append(String.format("%.2f грн", item.getPrice() * item.getQuantity())).append("</td>")
        .append("</tr>");
    }

    html.append("</tbody>")
      .append("</table>")
      .append("<div class='total'>")
      .append("<p>Загальна сума: ").append(String.format("%.2f грн", order.getTotalPrice())).append("</p>")
      .append("</div>")
      .append("</div>")
      .append("<div class='footer'>")
      .append("<p>Якщо у вас виникли питання щодо вашого замовлення, будь ласка, зв'яжіться з нашою службою підтримки.</p>")
      .append("<p>Дякуємо, що обрали нас!</p>")
      .append("</div>")
      .append("</div>")
      .append("</body>")
      .append("</html>");

    return html.toString();
  }
}

