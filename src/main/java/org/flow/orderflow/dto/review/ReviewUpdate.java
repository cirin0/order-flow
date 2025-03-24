package org.flow.orderflow.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class ReviewUpdate implements Serializable {
  private final Integer rating;
  private final String comment;
}
