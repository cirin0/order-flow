package org.flow.orderflow.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
public class ReviewCreate implements Serializable {
  private Long productId;
  private Long userId;
  private String content;
  private Integer rating;
}
