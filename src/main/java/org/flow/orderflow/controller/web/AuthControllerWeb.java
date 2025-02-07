
package org.flow.orderflow.controller.web;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.user.UserRegistrationDto;
import org.flow.orderflow.dto.user.UserDto;
import org.flow.orderflow.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthControllerWeb {
  private final UserService userService;

  @GetMapping("/login")
  public String showLoginForm() {
    return "auth/login";  // замість просто "login"
  }

  @PostMapping("/login")
  public String login(@RequestParam String email,
                      HttpSession session,
                      RedirectAttributes redirectAttributes) {
    if (userService.existsByEmail(email)) {
      UserDto user = userService.getAllUsers().stream()
        .filter(u -> u.getEmail().equals(email))
        .findFirst()
        .map(u -> {
          UserDto dto = new UserDto();
          dto.setId(u.getId());
          dto.setEmail(u.getEmail());
          dto.setFirstName(u.getFirstName());
          dto.setLastName(u.getLastName());
          return dto;
        })
        .orElseThrow();
      session.setAttribute("userEmail", email);
      return "redirect:/cart";  // Changed from "/cart/" + user.getId()
    }

    redirectAttributes.addFlashAttribute("error", "Користувача не знайдено");
    return "redirect:/login";
  }

  @GetMapping("/register")
  public String showRegistrationForm(Model model) {
    model.addAttribute("user", new UserRegistrationDto());
    return "auth/register";
  }

  @PostMapping("/register")
  public String register(@ModelAttribute UserRegistrationDto registrationDto,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
    try {
      UserDto userDto = userService.registerUser(registrationDto);
      // Зберігаємо email в сесії
      session.setAttribute("userEmail", userDto.getEmail());
      return "redirect:/cart";
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/register";
    }
  }

  @GetMapping("/logout")
  public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/login";
  }
}
