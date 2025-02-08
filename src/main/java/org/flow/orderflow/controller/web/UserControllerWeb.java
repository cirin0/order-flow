package org.flow.orderflow.controller.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.user.UserLoginDto;
import org.flow.orderflow.dto.user.UserRegistrationDto;
import org.flow.orderflow.dto.user.UserSessionDto;
import org.flow.orderflow.service.AuthenticationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserControllerWeb {

  private final AuthenticationService authenticationService;



  @GetMapping("/profile")
  public String dashboard(Model model, HttpSession session) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    model.addAttribute("user", user);
    return "dashboard";
  }

  @GetMapping("/logout")
  public String logout(HttpSession session) {
    session.removeAttribute("user");
    return "redirect:/user/login";
  }

}
