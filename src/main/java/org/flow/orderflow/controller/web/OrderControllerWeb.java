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
import org.flow.orderflow.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

  @GetMapping
  public String getAllOrders(Model model, HttpSession session) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    if (user == null) {
      return "redirect:/auth/login";
    }
    model.addAttribute("orders", orderService.getAllOrders());
    return "orders/list";
  }

  @GetMapping("/create")
  public String showCreateOrderPage(HttpSession session, Model model) {
    UserSessionDto userSession = (UserSessionDto) session.getAttribute("user");
    if (userSession == null) {
      return "redirect:/auth/login";
    }

    UserDto userDetails = userService.getUserById(userSession.getUserId());
    CartDto cart = cartService.getCartByUserId(userSession.getUserId());

    model.addAttribute("cart", cart);
    model.addAttribute("userDetails", userDetails);
    return "orders/create-order";
  }

  @GetMapping("/my")
  public String getMyOrders(Model model, HttpSession session) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    if (user == null) {
      return "redirect:/auth/login";
    }
    model.addAttribute("orders", orderService.getOrderByUserEmail(user.getEmail()));
    return "orders/my-orders";
  }

  @GetMapping("/{id}")
  public String getOrderDetails(@PathVariable Long id, Model model, HttpSession session) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    if (user == null) {
      return "redirect:/auth/login";
    }

    OrderDto order = orderService.getOrderById(id);
    model.addAttribute("order", order);
    model.addAttribute("statuses", OrderStatus.values());
    return "orders/details";
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
      redirectAttributes.addFlashAttribute("success", "Замовлення успішно скасовано");
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
}
