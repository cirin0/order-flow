package org.flow.orderflow.controller.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.cart.CartDto;
import org.flow.orderflow.dto.order.OrderDto;
import org.flow.orderflow.dto.user.UserDto;
import org.flow.orderflow.dto.user.UserSessionDto;
import org.flow.orderflow.model.OrderStatus;
import org.flow.orderflow.service.CartService;
import org.flow.orderflow.service.OrderService;
import org.flow.orderflow.service.PdfService;
import org.flow.orderflow.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*
   !!! ЦЕ ПРОСТО ТЕСТ !!!
   Можете міняти як треба. з цього прикладу все працює, але ви можете змінити його як вам зручно.
*/

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderControllerWeb {
  private final OrderService orderService;
  private final CartService cartService;
  private final UserService userService;
  private final PdfService pdfService;

  @GetMapping
  public String getAllOrders(Model model, HttpSession session) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    if (user == null) {
      return "redirect:/auth/login";
    }

    List<OrderDto> orders;
    try {
      if (user.getRole().name().equals("ADMIN")) {
        orders = orderService.getAllOrdersWithUserDetails();
      } else {
        orders = orderService.getOrdersByUserId(user.getUserId());
      }
      model.addAttribute("isAdmin", user.getRole().name().equals("ADMIN"));
      model.addAttribute("orders", orders);

      // Додаємо повідомлення, якщо замовлень немає
      if (orders.isEmpty() && !user.getRole().name().equals("ADMIN")) {
        model.addAttribute("info", "Ви ще не зробили жодного замовлення.");
      }
    } catch (Exception e) {
      model.addAttribute("error", "Помилка при завантаженні замовлень: " + e.getMessage());
      model.addAttribute("orders", new ArrayList<>());
    }

    return "orders/list";
  }

  @GetMapping("/create")
  public String showCreateOrderPage(HttpSession session, Model model) {
    UserSessionDto userSession = (UserSessionDto) session.getAttribute("user");
    if (userSession == null) {
      return "redirect:/auth/login";
    }

    // Якщо користувач адмін - перенаправляємо на список замовлень
    if ("ADMIN".equals(userSession.getRole())) {
      return "redirect:/orders";
    }

    UserDto userDetails = userService.getUserById(userSession.getUserId());
    CartDto cart = cartService.getCartByUserId(userSession.getUserId());

    model.addAttribute("cart", cart);
    model.addAttribute("userDetails", userDetails);
    return "orders/create-order";
  }

  @GetMapping("/{id}")
  public String getOrderDetails(@PathVariable Long id, Model model, HttpSession session) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    if (user == null) {
      return "redirect:/auth/login";
    }

    try {
      OrderDto order = orderService.getOrderById(id);

      // Перевіряємо чи має користувач доступ до цього замовлення
      if (!user.getRole().name().equals("ADMIN") && !order.getUserId().equals(user.getUserId())) {
        return "redirect:/orders";
      }

      model.addAttribute("order", order);
      model.addAttribute("statuses", OrderStatus.values());
      model.addAttribute("isAdmin", user.getRole().name().equals("ADMIN"));

      return "orders/details"; // Переконайтеся, що шлях до шаблону вірний
    } catch (Exception e) {
      model.addAttribute("error", "Помилка при завантаженні замовлення: " + e.getMessage());
      return "redirect:/orders";
    }
  }

  @PostMapping("/create")
  public String createOrder(HttpSession session, RedirectAttributes redirectAttributes) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    if (user == null) {
      return "redirect:/auth/login";
    }

    try {
      OrderDto orderDto = new OrderDto();
      orderDto.setUserId(user.getUserId());
      OrderDto createdOrder = orderService.createOrder(orderDto, user.getEmail());

      redirectAttributes.addFlashAttribute("success",
        "Замовлення успішно створено з ID: " + createdOrder.getId());
      return "redirect:/orders/" + createdOrder.getId();
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error",
        "Помилка створення замовлення: " + e.getMessage());
      return "redirect:/orders/create";
    }
  }

  @PostMapping("/{id}/status")
  public String updateOrderStatus(@PathVariable Long id,
                                  @RequestParam OrderStatus status,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    if (user == null) {
      return "redirect:/auth/login";
    }

    // Перевіряємо чи користувач є адміном
    if (!user.getRole().name().equals("ADMIN")) {
      redirectAttributes.addFlashAttribute("error",
        "У вас немає прав для зміни статусу замовлення");
      return "redirect:/orders/" + id;
    }

    try {
      orderService.updateOrderStatus(id, status);
      redirectAttributes.addFlashAttribute("success",
        "Статус замовлення успішно оновлено на " + status.getDescription());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error",
        "Помилка оновлення статусу: " + e.getMessage());
    }
    return "redirect:/orders/" + id;
  }

  @PostMapping("/{id}/cancel")
  public String cancelOrder(@PathVariable Long id,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    if (user == null) {
      return "redirect:/auth/login";
    }

    try {
      orderService.cancelOrder(id);
      redirectAttributes.addFlashAttribute("success", "Замовлення успішно скасовано. Ми звяжемось з Вами блищим ");
    } catch (IllegalStateException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/orders/" + id;
  }

  @PostMapping("/{id}/complete")
  public String completeOrder(@PathVariable Long id,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    if (user == null) {
      return "redirect:/auth/login";
    }

    try {
      orderService.completeOrder(id);
      redirectAttributes.addFlashAttribute("success", "Замовлення успішно завершено");
    } catch (IllegalStateException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/orders/" + id;
  }

  @PostMapping("/{id}/delete")
  public String deleteOrder(@PathVariable Long id,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    if (user == null) {
      return "redirect:/auth/login";
    }

    try {
      orderService.deleteOrder(id);
      redirectAttributes.addFlashAttribute("success", "Замовлення успішно видалено");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error",
        "Помилка видалення замовлення: " + e.getMessage());
    }
    return "redirect:/orders";
  }

  @GetMapping("/{id}/download-invoice")
  public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id, HttpSession session) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    try {
      OrderDto order = orderService.getOrderById(id);
      byte[] pdfContent = pdfService.generateInvoice(order);

      String dateStr = order.getOrderDate() != null
        ? order.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

      String filename = String.format("Чек-%s-Замовлення-%d.pdf", dateStr, order.getId());

      String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
        .replace("+", "%20");

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"");
      headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename);

      return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
