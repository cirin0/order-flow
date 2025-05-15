package org.flow.orderflow.controller.web;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.cart.CartDto;
import org.flow.orderflow.dto.cart.CartItemDto;
import org.flow.orderflow.dto.order.OrderDto;
import org.flow.orderflow.dto.user.UserSessionDto;
import org.flow.orderflow.service.CartService;
import org.flow.orderflow.service.OrderService;
import org.flow.orderflow.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

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
    model.addAttribute("pageTitle", "Корзина");
    model.addAttribute("isAuthenticated", user != null);

    if (user != null) {
      CartDto cart = cartService.getOrCreateCartByUserId(user.getUserId());
      model.addAttribute("cart", cart);

      if (!cart.getWarningMessages().isEmpty()) {
        model.addAttribute("warningMessages", cart.getWarningMessages());
      }
    }

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
      int stockQuantity = productService.getProductStock(productId);
      if (quantity > stockQuantity) {
        redirectAttributes.addFlashAttribute("errorMessage",
          "Недостатньо товару на складі. Доступно: " + stockQuantity);
        return "redirect:/cart";
      }

      UserSessionDto user = (UserSessionDto) session.getAttribute("user");
      CartDto cart = cartService.getOrCreateCartByUserId(user.getUserId());

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

  @Hidden
  @ResponseBody
  @PostMapping("/update")
  public ResponseEntity<?> updateCartItem(
    HttpSession session,
    @RequestParam Long itemId,
    @RequestParam Integer quantity
  ) {
    try {
      UserSessionDto user = (UserSessionDto) session.getAttribute("user");
      CartDto cart = cartService.getCartByUserId(user.getUserId());

      cartService.updateItemQuantity(cart.getId(), itemId, quantity);
      CartDto updatedCart = cartService.getCartByUserId(user.getUserId());

      return ResponseEntity.ok().body(Map.of(
        "success", true,
        "message", "Кошик успішно оновлено",
        "totalPrice", updatedCart.getTotalPrice(),
        "warningMessage", updatedCart.getWarningMessages().isEmpty() ? "" : updatedCart.getWarningMessages().getFirst()
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Не вдалося оновити кошик"));
    }
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
      redirectAttributes.addFlashAttribute("successMessage", "Товар видалено з кошику");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "не вдалося видалити товар");
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
      redirectAttributes.addFlashAttribute("successMessage", "Кошик очищено");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Не вдалося очистити кошик");
    }
    return "redirect:/cart";
  }
}
