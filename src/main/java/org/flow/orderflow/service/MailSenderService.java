package org.flow.orderflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
  public void sendOrderConfirmationMail(String to, String orderDetails) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject("Order Confirmation");
    message.setText(orderDetails);
    mailSender.send(message);
  }
}
