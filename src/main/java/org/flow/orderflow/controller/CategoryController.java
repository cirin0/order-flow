package org.flow.orderflow.controller;

import lombok.AllArgsConstructor;
import org.flow.orderflow.model.Category;
import org.flow.orderflow.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
  private final CategoryService categoryService;

  @GetMapping
  public ResponseEntity<List<Category>> getAllCategories() {
    return ResponseEntity.ok(categoryService.getAllCategories());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
    return ResponseEntity.ok(categoryService.getCategoryById(id));
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<Category> getCategoryByName(@PathVariable String name) {
    return ResponseEntity.ok(categoryService.getCategoryByName(name));
  }

  @PostMapping
  public ResponseEntity<Category> addCategory(@RequestBody Category category) {
    return ResponseEntity.ok(categoryService.addCategory(category));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
    Category updatedCategory = categoryService.updateCategory(id, category);
    return ResponseEntity.ok(updatedCategory);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.ok().build();
  }

}
