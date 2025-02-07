//хто прочитав той гей

package org.flow.orderflow.controller.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.cart.CartDto;
import org.flow.orderflow.dto.cart.CartItemDto;
import org.flow.orderflow.dto.user.UserDto;
import org.flow.orderflow.service.CartService;
import org.flow.orderflow.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartControllerWeb {

  private final CartService cartService;
  private final UserService userService;

  @GetMapping
  public String showCartPage(HttpSession session, Model model) {
    String userEmail = (String) session.getAttribute("userEmail");

    if (userEmail == null) {
      return "redirect:/login";
    }

    UserDto user = userService.getUserByEmail(userEmail);
    CartDto cart = cartService.getCartByUserId(user.getId());
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
      String userEmail = (String) session.getAttribute("userEmail");
      UserDto user = userService.getUserByEmail(userEmail);
      CartDto cart = cartService.getCartByUserId(user.getId());

      CartItemDto itemDto = new CartItemDto();
      itemDto.setProductId(productId);
      itemDto.setQuantity(quantity);

      cartService.addItemToCart(cart.getId(), itemDto);
      redirectAttributes.addFlashAttribute("successMessage", "Product added successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Failed to add product");
    }
    return "redirect:/cart";
  }

  @GetMapping("/{userId}")
  public String showCartPage(@PathVariable Long userId, Model model) {
    CartDto cart = cartService.getCartByUserId(userId);
    model.addAttribute("cart", cart);
    return "cart";
  }

  @PostMapping("/update")
  public String updateCartItem(
    HttpSession session,
    @RequestParam Long itemId,
    @RequestParam Integer quantity,
    RedirectAttributes redirectAttributes
  ) {
    try {
      String userEmail = (String) session.getAttribute("userEmail");
      UserDto user = userService.getUserByEmail(userEmail);
      CartDto cart = cartService.getCartByUserId(user.getId());

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
      String userEmail = (String) session.getAttribute("userEmail");
      UserDto user = userService.getUserByEmail(userEmail);
      CartDto cart = cartService.getCartByUserId(user.getId());

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
      String userEmail = (String) session.getAttribute("userEmail");
      UserDto user = userService.getUserByEmail(userEmail);
      CartDto cart = cartService.getCartByUserId(user.getId());

      cartService.clearCart(cart.getId());
      redirectAttributes.addFlashAttribute("successMessage", "Cart cleared successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Failed to clear cart");
    }
    return "redirect:/cart";
  }
}
