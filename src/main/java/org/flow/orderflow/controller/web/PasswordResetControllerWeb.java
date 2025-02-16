// PasswordResetControllerWeb.java
package org.flow.orderflow.controller.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.password.ForgotPasswordDto;
import org.flow.orderflow.dto.password.ResetPasswordDto;
import org.flow.orderflow.dto.password.ValidateCodeDto;
import org.flow.orderflow.service.PasswordResetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordResetControllerWeb {
  private final PasswordResetService passwordResetService;

  @GetMapping("/forgot")
  public String showForgotPasswordForm(HttpSession session, Model model) {
    if (session.getAttribute("user") != null) {
      return "redirect:/";
    }
    model.addAttribute("forgotPasswordDto", new ForgotPasswordDto(""));
    return "password/forgot-password";
  }

  @PostMapping("/forgot")
  public String processForgotPassword(
    @Valid @ModelAttribute ForgotPasswordDto request,
    BindingResult bindingResult,
    RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute("error",
        "Будь ласка, введіть коректний email");
      return "redirect:/password/forgot";
    }

    try {
      passwordResetService.initiatePasswordReset(request);
      redirectAttributes.addFlashAttribute("message",
        "Код для скидання паролю відправлено на вашу електронну пошту");
      redirectAttributes.addFlashAttribute("email", request.email());
      return "redirect:/password/reset?email=" + request.email();
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error",
        "Помилка при відправці коду. Перевірте email та спробуйте знову");
      return "redirect:/password/forgot";
    }
  }

  @GetMapping("/reset")
  public String showResetPasswordForm(
    @RequestParam("email") String email,
    HttpSession session,
    Model model) {
    if (session.getAttribute("user") != null) {
      return "redirect:/";
    }
    model.addAttribute("validateCodeDto", new ValidateCodeDto(email, ""));
    return "password/enter-code";
  }

  @PostMapping("/validate-code")
  public String validateCode(
    @Valid @ModelAttribute ValidateCodeDto request,
    BindingResult bindingResult,
    RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute("error", "Невірний формат коду");
      redirectAttributes.addFlashAttribute("email", request.email());
      return "redirect:/password/reset?email=" + request.email();
    }

    if (passwordResetService.validateResetCode(request)) {
      redirectAttributes.addFlashAttribute("email", request.email());
      redirectAttributes.addFlashAttribute("code", request.code());
      return "redirect:/password/new-password?email=" +
        request.email() + "&code=" + request.code();
    } else {
      redirectAttributes.addFlashAttribute("error", "Невірний або застарілий код");
      redirectAttributes.addFlashAttribute("email", request.email());
      return "redirect:/password/reset?email=" + request.email();
    }
  }

  @GetMapping("/new-password")
  public String showNewPasswordForm(
    @RequestParam("email") String email,
    @RequestParam("code") String code,
    HttpSession session,
    Model model) {
    if (session.getAttribute("user") != null) {
      return "redirect:/";
    }
    model.addAttribute("resetPasswordDto",
      new ResetPasswordDto(email, code, "", ""));
    return "password/reset-password";
  }

  @PostMapping("/reset")
  public String resetPassword(
    @Valid @ModelAttribute ResetPasswordDto request,
    BindingResult bindingResult,
    RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute("error",
        "Будь ласка, перевірте правильність введених даних");
      return "redirect:/password/new-password?email=" +
        request.email() + "&code=" + request.code();
    }

    if (!request.isPasswordsMatch()) {
      redirectAttributes.addFlashAttribute("error", "Паролі не співпадають");
      return "redirect:/password/new-password?email=" +
        request.email() + "&code=" + request.code();
    }

    try {
      passwordResetService.resetPassword(request);
      redirectAttributes.addFlashAttribute("message",
        "Пароль успішно змінено. Тепер ви можете увійти з новим паролем.");
      return "password/success";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Помилка при зміні паролю");
      return "redirect:/password/new-password?email=" +
        request.email() + "&code=" + request.code();
    }
  }
}
