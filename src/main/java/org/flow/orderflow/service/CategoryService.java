package org.flow.orderflow.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.category.CategoryDto;
import org.flow.orderflow.exception.NotFound;
import org.flow.orderflow.mapper.CategoryMapper;
import org.flow.orderflow.model.Category;
import org.flow.orderflow.repository.CategoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public List<CategoryDto> getAllCategories() {
    List<Category> categories = categoryRepository.findAll();
    return categoryMapper.toDtoList(categories);
  }

  public CategoryDto getCategoryById(Long id) {
    return categoryRepository.findById(id)
      .map(categoryMapper::toDto)
      .orElseThrow(() -> new NotFound("Category not found with id: " + id));
  }

  public CategoryDto getCategoryByName(String name) {
    Category category = categoryRepository.findByName(name);
    if (category == null) {
      throw new NotFound("Category not found with name: " + name);
    }
    return categoryMapper.toDto(category);
  }

  public CategoryDto addCategory(CategoryDto categoryDto) {
    Category existingCategory = categoryRepository.findByName(categoryDto.getName());
    if (existingCategory != null) {
      throw new NotFound("Category already exists with name: " + categoryDto.getName());
    }
    Category category = categoryMapper.toEntity(categoryDto);
    return categoryMapper.toDto(categoryRepository.save(category));
  }

  public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
    Category existingCategory = categoryRepository.findById(id)
      .orElseThrow(() -> new NotFound("Category not found with id: " + id));
    Category category = categoryMapper.partialUpdate(categoryDto, existingCategory);
    return categoryMapper.toDto(categoryRepository.save(category));
  }

  public void deleteCategory(Long id) {
    Category category = categoryRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    categoryRepository.delete(category);
  }

  public List<CategoryDto> searchCategories(String query, int size) {
    Pageable pageable = PageRequest.of(0, size);
    return categoryRepository.searchByNameContaining(query, pageable).stream()
      .map(categoryMapper::toDto)
      .collect(Collectors.toList());
  }

}
