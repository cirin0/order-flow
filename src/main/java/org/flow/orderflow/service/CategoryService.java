package org.flow.orderflow.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.exception.NotFound;
import org.flow.orderflow.model.Category;
import org.flow.orderflow.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;

  public List<Category> getAllCategories() {
    return categoryRepository.findAll();
  }

  public Category getCategoryById(Long id) {
    return categoryRepository.findById(id)
      .orElseThrow(() -> new NotFound("Category not found with id: " + id));
  }

  public Category getCategoryByName(String name) {
    Category category = categoryRepository.findByName(name);
    if (category == null) {
      throw new NotFound("Category not found with name: " + name);
    }
    return category;
  }

  public Category addCategory(Category category) {
    Category existingCategory = categoryRepository.findByName(category.getName());
    if (existingCategory != null) {
      return existingCategory;
    }
    return categoryRepository.save(category);
  }

  public Category updateCategory(Long id, Category category) {
    Category existingCategory = categoryRepository.findById(id)
      .orElseThrow(() -> new NotFound("Category not found with id: " + id));
    if (category.getName() != null) {
      existingCategory.setName(category.getName());
    }
    if (category.getImageUrl() != null) {
      existingCategory.setImageUrl(category.getImageUrl());
    }
    return categoryRepository.save(existingCategory);
  }

  public void deleteCategory(Long id) {
    Category category = categoryRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    categoryRepository.delete(category);
  }
}
