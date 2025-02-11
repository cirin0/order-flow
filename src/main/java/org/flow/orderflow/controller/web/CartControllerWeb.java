package org.flow.orderflow.controller.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.cart.CartDto;
import org.flow.orderflow.dto.cart.CartItemDto;
import org.flow.orderflow.dto.order.OrderDto;
import org.flow.orderflow.dto.user.UserSessionDto;
import org.flow.orderflow.service.CartService;
import org.flow.orderflow.service.OrderService;
import org.flow.orderflow.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartControllerWeb {
  private final CartService cartService;
  private final ProductService productService;
  private final OrderService orderService;

  @GetMapping
  public String showCartPage(HttpSession session, Model model) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");

    if (user == null) {
      return "redirect:/auth/login";
    }

    CartDto cart = cartService.getOrCreateCartByUserId(user.getUserId());
    model.addAttribute("cart", cart);
    return "cart/cart";
  }

  @PostMapping("/add")
  public String addItemToCart(
    HttpSession session,
    @RequestParam Long productId,
    @RequestParam Integer quantity,
    RedirectAttributes redirectAttributes
  ) {
    try {
      // Перевірка наявності на складі
      int stockQuantity = productService.getProductStock(productId);
      if (quantity > stockQuantity) {
        redirectAttributes.addFlashAttribute("errorMessage",
          "Недостатньо товару на складі. Доступно: " + stockQuantity);
        return "redirect:/cart";
      }

      UserSessionDto user = (UserSessionDto) session.getAttribute("user");
      CartDto cart = cartService.getOrCreateCartByUserId(user.getUserId());

      // Перевірка загальної кількості з урахуванням існуючих товарів у кошику
      int existingQuantity = cart.getItems().stream()
        .filter(item -> item.getProductId().equals(productId))
        .mapToInt(CartItemDto::getQuantity)
        .sum();

      if (existingQuantity + quantity > stockQuantity) {
        redirectAttributes.addFlashAttribute("errorMessage",
          "Перевищено доступну кількість товару на складі");
        return "redirect:/cart";
      }

      CartItemDto itemDto = new CartItemDto();
      itemDto.setProductId(productId);
      itemDto.setQuantity(quantity);

      cartService.addItemToCart(cart.getId(), itemDto);
      redirectAttributes.addFlashAttribute("successMessage", "Товар успішно додано");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Помилка додавання товару");
    }
    return "redirect:/cart";
  }

  @PostMapping("/create-order")
  public String createOrderFromCart(HttpSession session, RedirectAttributes redirectAttributes) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    if (user == null) {
      return "redirect:/auth/login";
    }
    try {
      CartDto cart = cartService.getCartByUserId(user.getUserId());

      if (cart.getItems().isEmpty()) {
        redirectAttributes.addFlashAttribute("errorMessage", "Кошик порожній");
        return "redirect:/cart";
      }

      OrderDto orderDto = new OrderDto();
      orderDto.setUserId(user.getUserId());
      OrderDto createdOrder = orderService.createOrder(orderDto, user.getEmail());

      redirectAttributes.addFlashAttribute("success",
        "Замовлення успішно створено з ID: " + createdOrder.getId());
      return "redirect:/orders/" + createdOrder.getId();
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error",
        "Помилка створення замовлення: " + e.getMessage());
      return "redirect:/cart";
    }
  }


  @GetMapping("/{userId}")
  public String showCartPage(@PathVariable Long userId, Model model) {
    CartDto cart = cartService.getCartByUserId(userId);
    model.addAttribute("cart", cart);
    return "cart/cart";
  }

  @PostMapping("/update")
  public String updateCartItem(
    HttpSession session,
    @RequestParam Long itemId,
    @RequestParam Integer quantity,
    RedirectAttributes redirectAttributes
  ) {
    try {
      UserSessionDto user = (UserSessionDto) session.getAttribute("user");
      CartDto cart = cartService.getCartByUserId(user.getUserId());

      cartService.updateItemQuantity(cart.getId(), itemId, quantity);
      redirectAttributes.addFlashAttribute("successMessage", "Cart updated successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Failed to update cart");
    }
    return "redirect:/cart";
  }

  @PostMapping("/remove")
  public String removeCartItem(
    HttpSession session,
    @RequestParam Long itemId,
    RedirectAttributes redirectAttributes
  ) {
    try {
      UserSessionDto user = (UserSessionDto) session.getAttribute("user");
      CartDto cart = cartService.getCartByUserId(user.getUserId());

      cartService.removeItemFromCart(cart.getId(), itemId);
      redirectAttributes.addFlashAttribute("successMessage", "Item removed successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Failed to remove item");
    }
    return "redirect:/cart";
  }

  @PostMapping("/clear")
  public String clearCart(
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    try {
      UserSessionDto user = (UserSessionDto) session.getAttribute("user");
      CartDto cart = cartService.getCartByUserId(user.getUserId());

      cartService.clearCart(cart.getId());
      redirectAttributes.addFlashAttribute("successMessage", "Cart cleared successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Failed to clear cart");
    }
    return "redirect:/cart";
  }
}
