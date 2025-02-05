package org.flow.orderflow.controller.web;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.category.CategoryDto;
import org.flow.orderflow.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryControllerWeb {

  private final CategoryService categoryService;

  @GetMapping
  public String listCategories(Model model) {
    model.addAttribute("categories", categoryService.getAllCategories());
    return "categories/category-list";
  }

  @GetMapping("/add")
  public String showAddCategoryForm(Model model) {
    model.addAttribute("category", new CategoryDto());
    return "categories/category-add";
  }

  @PostMapping("/add")
  public String addCategory(@ModelAttribute CategoryDto categoryDto) {
    categoryService.addCategory(categoryDto);
    return "redirect:/categories";
  }

  @GetMapping("/edit/{id}")
  public String showEditCategoryForm(@PathVariable Long id, Model model) {
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
  public String deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return "redirect:/categories";
  }
}
