package org.flow.orderflow.controller.api;

import lombok.AllArgsConstructor;
import org.flow.orderflow.dto.category.CategoryDto;
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
  public ResponseEntity<List<CategoryDto>> getAllCategories() {
    return ResponseEntity.ok(categoryService.getAllCategories());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
    return ResponseEntity.ok(categoryService.getCategoryById(id));
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<CategoryDto> getCategoryByName(@PathVariable String name) {
    return ResponseEntity.ok(categoryService.getCategoryByName(name));
  }

  @PostMapping
  public ResponseEntity<CategoryDto> addCategory(@RequestBody CategoryDto categoryDto) {
    return ResponseEntity.ok(categoryService.addCategory(categoryDto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @RequestBody CategoryDto categoryDto) {
    CategoryDto updatedCategory = categoryService.updateCategory(id, categoryDto);
    return ResponseEntity.ok(updatedCategory);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.ok().build();
  }

}
