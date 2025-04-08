package org.flow.orderflow.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class ReviewCreate implements Serializable {
  private String content;
  private Integer rating;
  private final Long productId;
  private final Long userId;
}
