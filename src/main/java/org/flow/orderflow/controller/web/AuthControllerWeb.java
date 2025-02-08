
package org.flow.orderflow.controller.web;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.user.UserLoginDto;
import org.flow.orderflow.dto.user.UserRegistrationDto;
import org.flow.orderflow.dto.user.UserDto;
import org.flow.orderflow.dto.user.UserSessionDto;
import org.flow.orderflow.service.AuthenticationService;
import org.flow.orderflow.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthControllerWeb {
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
      return "redirect:/auth/login";
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


}
