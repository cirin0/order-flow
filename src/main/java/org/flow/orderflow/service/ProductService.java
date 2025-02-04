package org.flow.orderflow.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.flow.orderflow.model.Category;
import org.flow.orderflow.model.Product;
import org.flow.orderflow.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {
  public final ProductRepository productRepository;
  private final CategoryService categoryService;

  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  public Product getProductById(Long id) {
    return productRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
  }

  public Product addProduct(Product product) {
    if (product.getCategory() != null) {
      Category category = categoryService.getCategoryById(product.getCategory().getId());
      product.setCategory(category);
    }
    return productRepository.save(product);
  }

  public Optional<Product> updateProduct(Long id, Product product) {
    Product existingProduct = productRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    Category category = categoryService.getCategoryById(product.getCategory().getId());
    existingProduct.setCategory(category);
    existingProduct.setName(product.getName());
    existingProduct.setPrice(product.getPrice());
    existingProduct.setStock(product.getStock());
    return Optional.of(productRepository.save(existingProduct));
  }

  public void deleteProduct(Long id) {
    Product product = productRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    productRepository.delete(product);
  }
}
