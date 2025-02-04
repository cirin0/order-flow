package org.flow.orderflow.service;

import lombok.AllArgsConstructor;
import org.flow.orderflow.exception.NotFound;
import org.flow.orderflow.model.Category;
import org.flow.orderflow.model.Product;
import org.flow.orderflow.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {
  private final ProductRepository productRepository;
  private final CategoryService categoryService;

  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  public Product getProductById(Long id) {
    return productRepository.findById(id)
      .orElseThrow(() -> new NotFound("Product not found with id: " + id));
  }

  public Product getProductByName(String name) {
    Product product = productRepository.findByName(name);
    if (product == null) {
      throw new NotFound("Product not found with name: " + name);
    }
    return product;
  }

  public List<Product> getProductsByCategoryId(Long categoryId) {
    Product product = productRepository.findByCategoryId(categoryId);
    if (product == null) {
      throw new NotFound("Product not found with category id: " + categoryId);
    }
    return List.of(product);
  }

  public List<Product> getProductsByCategoryName(String categoryName) {
    Category category = categoryService.getCategoryByName(categoryName);
    return productRepository.findByCategory(category);
  }

  public Product getProductNameById(Long id) {
    return productRepository.findById(id)
      .orElseThrow(() -> new NotFound("Product not found with id: " + id));
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
      .orElseThrow(() -> new NotFound("Product not found with id: " + id));
    if (product.getName() != null) {
      existingProduct.setName(product.getName());
    }
    if (product.getDescription() != null) {
      existingProduct.setDescription(product.getDescription());
    }
    if (product.getPrice() != null) {
      existingProduct.setPrice(product.getPrice());
    }
    if (product.getCategory() != null) {
      Category category = categoryService.getCategoryById(product.getCategory().getId());
      existingProduct.setCategory(category);
    }
    return Optional.of(productRepository.save(existingProduct));
  }

  public void deleteProduct(Long id) {
    Product product = productRepository.findById(id)
      .orElseThrow(() -> new NotFound("Product not found with id: " + id));
    productRepository.delete(product);
  }
}
