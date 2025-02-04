package org.flow.orderflow.controller;

import lombok.AllArgsConstructor;
import org.flow.orderflow.model.Product;
import org.flow.orderflow.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
  private final ProductService productService;

  @GetMapping
  public ResponseEntity<List<Product>> getAllProducts() {
    return ResponseEntity.ok(productService.getAllProducts());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Product> getProductById(@PathVariable Long id) {
    return ResponseEntity.ok(productService.getProductById(id));
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<Product> getProductByName(@PathVariable String name) {
    return ResponseEntity.ok(productService.getProductByName(name));
  }

  @GetMapping("/category/{categoryId}")
  public ResponseEntity<List<Product>> getProductsByCategoryId(@PathVariable Long categoryId) {
    return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId));
  }

  @GetMapping("/category/name/{categoryName}")
  public ResponseEntity<List<Product>> getProductsByCategoryName(@PathVariable String categoryName) {
    return ResponseEntity.ok(productService.getProductsByCategoryName(categoryName));
  }

  @GetMapping("/named/{id}")
  public ResponseEntity<Product> getProductNameById(@PathVariable Long id) {
    return ResponseEntity.ok(productService.getProductNameById(id));
  }

  @PostMapping
  public ResponseEntity<Product> addProduct(@RequestBody Product product) {
    return ResponseEntity.ok(productService.addProduct(product));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
    Product updatedProduct = productService.updateProduct(id, product)
      .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    return ResponseEntity.ok(updatedProduct);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.ok().build();
  }
}
