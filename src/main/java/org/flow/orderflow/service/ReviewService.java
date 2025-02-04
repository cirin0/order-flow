package org.flow.orderflow.service;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.repository.ReviewRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {
  private final ReviewRepository reviewRepository;
}
