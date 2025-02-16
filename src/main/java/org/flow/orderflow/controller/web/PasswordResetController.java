package org.flow.orderflow.controller.web;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.PasswordResetRequestDto;
import org.flow.orderflow.dto.PasswordResetVerifyDto;
import org.flow.orderflow.service.AuthenticationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class PasswordResetController {
  private final AuthenticationService authenticationService;

  @GetMapping("/forgot-password")
  public String showForgotPasswordPage() {
    return "forgot-password";
  }

  @PostMapping("/forgot-password")
  public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
    try {
      PasswordResetRequestDto requestDto = new PasswordResetRequestDto(email);
      authenticationService.requestPasswordReset(requestDto);
      redirectAttributes.addFlashAttribute("successMessage",
        "Код підтвердження надіслано на вашу електронну пошту");
      return "redirect:/user/reset-password?email=" + email;
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
      return "redirect:/user/forgot-password";
    }
  }

  @GetMapping("/reset-password")
  public String showResetPasswordForm(@RequestParam("email") String email, Model model) {
    model.addAttribute("email", email);
    return "reset-password";
  }

  @PostMapping("/reset-password")
  public String processResetPassword(@RequestParam("email") String email,
                                     @RequestParam("verificationCode") String verificationCode,
                                     @RequestParam("newPassword") String newPassword,
                                     RedirectAttributes redirectAttributes) {
    try {
      PasswordResetVerifyDto verifyDto = new PasswordResetVerifyDto(email, verificationCode, newPassword);
      authenticationService.verifyAndResetPassword(verifyDto);
      redirectAttributes.addFlashAttribute("successMessage", "Пароль успішно змінено");
      return "redirect:/auth/login";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
      redirectAttributes.addFlashAttribute("email", email);
      return "redirect:/user/reset-password?email=" + email;
    }
  }
}
