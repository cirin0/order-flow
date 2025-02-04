package org.flow.orderflow.repository;

import org.flow.orderflow.model.Category;
import org.flow.orderflow.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
  Product findByName(String name);

  Product findByCategoryId(Long categoryId);

  List<Product> findByCategory(Category category);
}
