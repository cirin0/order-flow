package org.flow.orderflow.controller.api;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.review.ReviewCreate;
import org.flow.orderflow.dto.review.ReviewDto;
import org.flow.orderflow.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
  private final ReviewService reviewService;

  @GetMapping
  public ResponseEntity<List<ReviewDto>> getAllReviews() {
    return ResponseEntity.ok(reviewService.getAllReviews());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ReviewDto> getReviewById(@PathVariable Long id) {
    return ResponseEntity.ok(reviewService.getReviewById(id));
  }

  @GetMapping("/product/{productId}")
  public ResponseEntity<List<ReviewDto>> getReviewsByProductId(@PathVariable Long productId) {
    return ResponseEntity.ok(reviewService.getReviewsByProductId(productId));
  }

  @GetMapping("/product/name/{productName}")
  public ResponseEntity<List<ReviewDto>> getReviewsByProductName(@PathVariable String productName) {
    return ResponseEntity.ok(reviewService.getReviewsByProductName(productName));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<ReviewDto>> getReviewsByUserId(@PathVariable Long userId) {
    return ResponseEntity.ok(reviewService.getReviewsByUserId(userId));
  }

  @GetMapping("/user/{userId}/product/{productId}")
  public ResponseEntity<ReviewDto> getReviewByUserAndProduct(
    @PathVariable Long userId,
    @PathVariable Long productId) {
    return ResponseEntity.ok(reviewService.getReviewByUserAndProduct(userId, productId));
  }

  @PostMapping
  public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewCreate reviewCreate) {
    return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(reviewCreate));
  }

  @PutMapping
  public ResponseEntity<ReviewDto> updateReview(@RequestBody ReviewCreate reviewUpdate) {
    return ResponseEntity.ok(reviewService.updateReview(reviewUpdate));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
    reviewService.deleteReview(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteAllReviews() {
    reviewService.deleteAllReviews();
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/product/{productId}")
  public ResponseEntity<Void> deleteReviewsByProductId(@PathVariable Long productId) {
    reviewService.deleteReviewsByProductId(productId);
    return ResponseEntity.noContent().build();
  }

}
