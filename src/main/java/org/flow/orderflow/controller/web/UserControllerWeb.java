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

  @GetMapping("/login")
  public String login(Model model) {
    model.addAttribute("userLoginDto", new UserLoginDto());
    return "login";
  }

  @GetMapping("/register")
  public String register(HttpSession session) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    if (user != null) {
      return "redirect:/user/profile";
    }
    return "register";
  }

  @PostMapping("/register")
  public String register(Model model, @ModelAttribute UserRegistrationDto userRegistrationDto) {
    try {
      authenticationService.registerUser(userRegistrationDto);
      model.addAttribute("success", "Користувач успішно зареєстрований!");
      return "redirect:/user/login";
    } catch (Exception e) {
      model.addAttribute("error", "Користувач з такою поштою вже існує!");
      return "register";
    }
  }

  @PostMapping("/login")
  public String login(Model model, @ModelAttribute UserLoginDto userLoginDto, HttpSession session) {
    try {
      UserSessionDto user = authenticationService.login(userLoginDto);
      session.setAttribute("user", user);
      return "redirect:/user/profile";
    } catch (Exception e) {
      model.addAttribute("error", "Невірний логін або пароль!");
      return "login";
    }
  }

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
