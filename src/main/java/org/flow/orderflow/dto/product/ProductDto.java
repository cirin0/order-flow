package org.flow.orderflow.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
  private Long id;
  private String name;
  private String imageUrl;
  private String description;
  private Double price;
  private Integer stock;
  private LocalDateTime createdAt;
  private Long categoryId;
  private String categoryName;
  private Double averageRating;
}
