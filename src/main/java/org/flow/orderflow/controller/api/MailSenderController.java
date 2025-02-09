package org.flow.orderflow.controller.api;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.service.MailSenderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailSenderController {
  private final MailSenderService mailSenderService;

  @PostMapping("/send")
  public void sendMail() {
    mailSenderService.sendMail(
      "пошта", "subject", "text");
  }
}
