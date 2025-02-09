package org.flow.orderflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.flow.orderflow.dto.category.CategoryDto;
import org.flow.orderflow.dto.product.ProductDto;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchResultDto {
  private List<ProductDto> products;
  private List<CategoryDto> categories;
}
