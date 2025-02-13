package org.flow.orderflow.controller.api;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.SearchResultDto;
import org.flow.orderflow.dto.category.CategoryDto;
import org.flow.orderflow.dto.product.ProductDto;
import org.flow.orderflow.service.CategoryService;
import org.flow.orderflow.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
  private final ProductService productService;
  private final CategoryService categoryService;

  @GetMapping
  public SearchResultDto search(@RequestParam String query) {
    int size = 10;
    List<ProductDto> products = productService.searchProducts(query, size);
    List<CategoryDto> categories = categoryService.searchCategories(query, size);

    return new SearchResultDto(products, categories);
  }
}
