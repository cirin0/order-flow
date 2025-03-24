package org.flow.orderflow.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link org.flow.orderflow.model.Review}
 */
@AllArgsConstructor
@Getter
@Builder
public class ReviewDto implements Serializable {
  private final Long id;
  private final String content;
  private final Integer rating;
  private final Long productId;
  private final String productName;
  private final Long userId;
  private final String userFirst_name;
  private final String userLast_name;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;
}
