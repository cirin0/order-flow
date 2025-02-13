package org.flow.orderflow.controller.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.user.UserDto;
import org.flow.orderflow.dto.user.UserSessionDto;
import org.flow.orderflow.service.AuthenticationService;
import org.flow.orderflow.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserControllerWeb {
  private final AuthenticationService authenticationService;
  private final UserService userService;

  @GetMapping("/profile")
  public String dashboard(Model model, HttpSession session) {
    UserSessionDto sessionUser = (UserSessionDto) session.getAttribute("user");
    if (sessionUser == null) {
      return "redirect:/auth/login";
    }

    UserDto userDetails = userService.getUserById(sessionUser.getUserId());
    model.addAttribute("userDetails", userDetails);

    return "user/profile";
  }

  @GetMapping("/edit-profile")
  public String editProfile(Model model, HttpSession session) {
    UserSessionDto sessionUser = (UserSessionDto) session.getAttribute("user");
    if (sessionUser == null) {
      return "redirect:/auth/login";
    }

    UserDto userDetails = userService.getUserById(sessionUser.getUserId());
    model.addAttribute("userDetails", userDetails);

    return "user/edit-profile";
  }

  @PostMapping("/update")
  public String updateProfile(@ModelAttribute("userDetails") UserDto userDto,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
    try {
      UserSessionDto sessionUser = (UserSessionDto) session.getAttribute("user");
      if (sessionUser == null) {
        return "redirect:/auth/login";
      }

      UserDto currentUser = userService.getUserById(sessionUser.getUserId());
      userDto.setId(sessionUser.getUserId());
      userDto.setEmail(currentUser.getEmail());
      userDto.setRole(currentUser.getRole());

      userService.updateUser(sessionUser.getUserId(), userDto);

      if (userDto.getAddress() != null) {
        userService.addOrUpdateDeliveryAddress(sessionUser.getUserId(), userDto.getAddress());
      }
      redirectAttributes.addFlashAttribute("successMessage", "Профіль успішно оновлено!");
      return "redirect:/user/profile";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage",
        "Помилка при оновленні профілю: " + e.getMessage());
      return "redirect:/user/edit-profile";
    }
  }

  @GetMapping("/logout")
  public String logout(HttpSession session) {
    String sessionToken = (String) session.getAttribute("sessionToken");
    if (sessionToken != null) {
      authenticationService.logout(sessionToken);
    }
    session.invalidate();
    return "redirect:/auth/login";
  }
}
