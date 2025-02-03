package org.flow.orderflow.service;

import lombok.AllArgsConstructor;
import org.flow.orderflow.model.Category;
import org.flow.orderflow.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;

  public Category getCategoryById(Long id) {
    return categoryRepository.findById(id).orElse(null);
  }

  public List<Category> getAllCategories() {
    return categoryRepository.findAll();
  }
}
