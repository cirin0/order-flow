package org.flow.orderflow.controller.api;

import lombok.AllArgsConstructor;
import org.flow.orderflow.dto.product.ProductDto;
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
  public ResponseEntity<List<ProductDto>> getAllProducts() {
    return ResponseEntity.ok(productService.getAllProducts());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
    return ResponseEntity.ok(productService.getProductById(id));
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<ProductDto> getProductByName(@PathVariable String name) {
    return ResponseEntity.ok(productService.getProductByName(name));
  }

  @GetMapping("/category/{categoryId}")
  public ResponseEntity<List<ProductDto>> getProductsByCategoryId(@PathVariable Long categoryId) {
    return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId));
  }

  @GetMapping("/category/name/{categoryName}")
  public ResponseEntity<List<ProductDto>> getProductsByCategoryName(@PathVariable String categoryName) {
    return ResponseEntity.ok(productService.getProductsByCategoryName(categoryName));
  }

  @GetMapping("/named/{id}")
  public ResponseEntity<ProductDto> getProductNameById(@PathVariable Long id) {
    return ResponseEntity.ok(productService.getProductNameById(id));
  }

  @PostMapping
  public ResponseEntity<ProductDto> addProduct(@RequestBody ProductDto productDto) {
    return ResponseEntity.ok(productService.addProduct(productDto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
    ProductDto updatedProduct = productService.updateProduct(id, productDto);
    return ResponseEntity.ok(updatedProduct);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.ok().build();
  }
}
