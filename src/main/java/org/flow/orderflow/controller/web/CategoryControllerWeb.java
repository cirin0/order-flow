package org.flow.orderflow.controller.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.category.CategoryDto;
import org.flow.orderflow.dto.product.ProductDto;
import org.flow.orderflow.dto.user.UserSessionDto;
import org.flow.orderflow.service.CategoryService;
import org.flow.orderflow.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryControllerWeb {

  private final CategoryService categoryService;
  private final ProductService productService;

  @GetMapping
  public String listCategories(Model model) {
    model.addAttribute("categories", categoryService.getAllCategories());
    return "categories/category-list";
  }

  @GetMapping("/add")
  public String showAddCategoryForm(Model model, HttpSession session) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    if (user == null || !user.getRole().name().equals("ADMIN")) {
      return "redirect:/";
    }
    model.addAttribute("category", new CategoryDto());
    return "categories/category-add";
  }


  @PostMapping("/add")
  public String addCategory(@ModelAttribute CategoryDto categoryDto) {
    categoryService.addCategory(categoryDto);
    return "redirect:/categories";
  }


  @GetMapping("/edit/{id}")
  public String showEditCategoryForm(@PathVariable Long id, Model model, HttpSession session) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    if (user == null || !user.getRole().name().equals("ADMIN")) {
      return "redirect:/";
    }
    CategoryDto categoryDto = categoryService.getCategoryById(id);
    model.addAttribute("category", categoryDto);
    return "categories/category-edit";
  }

  @PostMapping("/edit/{id}")
  public String updateCategory(@PathVariable Long id,
                               @ModelAttribute CategoryDto categoryDto) {
    categoryService.updateCategory(id, categoryDto);
    return "redirect:/categories";
  }

  @GetMapping("/delete/{id}")
  public String deleteCategory(@PathVariable Long id, HttpSession session) {
    UserSessionDto user = (UserSessionDto) session.getAttribute("user");
    if (user == null || !user.getRole().name().equals("ADMIN")) {
      return "redirect:/";
    }
    categoryService.deleteCategory(id);
    return "redirect:/categories";
  }

  @GetMapping("/{id}/products")
  public String showCategoryProducts(@PathVariable Long id, Model model) {
    CategoryDto category = categoryService.getCategoryById(id);
    List<ProductDto> products = productService.getProductsByCategoryId(id);

    model.addAttribute("categoryName", category.getName());
    model.addAttribute("products", products);
    return "categories/category-products";
  }
}
