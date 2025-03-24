package org.flow.orderflow.service;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.review.ReviewCreate;
import org.flow.orderflow.dto.review.ReviewDto;
import org.flow.orderflow.exception.NotFound;
import org.flow.orderflow.exception.ReviewAlreadyExists;
import org.flow.orderflow.mapper.ReviewMapper;
import org.flow.orderflow.model.Product;
import org.flow.orderflow.model.Review;
import org.flow.orderflow.model.User;
import org.flow.orderflow.repository.ProductRepository;
import org.flow.orderflow.repository.ReviewRepository;
import org.flow.orderflow.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
  private final ReviewRepository reviewRepository;
  private final ReviewMapper reviewMapper;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;

  public List<ReviewDto> getAllReviews() {
    List<Review> reviews = reviewRepository.findAll();
    return reviewMapper.toDtoList(reviews);
  }

  public ReviewDto getReviewById(Long id) {
    return reviewRepository.findById(id)
      .map(reviewMapper::toDto)
      .orElseThrow(() -> new NotFound("Review not found with id: " + id));
  }

  public List<ReviewDto> getReviewsByProductId(Long productId) {
    List<Review> reviews = reviewRepository.findByProductId(productId);
    return reviewMapper.toDtoList(reviews);
  }

  public List<ReviewDto> getReviewsByProductName(String productName) {
    List<Review> reviews = reviewRepository.findByProductName(productName);
    return reviewMapper.toDtoList(reviews);
  }

  public List<ReviewDto> getReviewsByUserId(Long userId) {
    List<Review> reviews = reviewRepository.findByUserId(userId);
    return reviewMapper.toDtoList(reviews);
  }

  public ReviewDto getReviewByUserAndProduct(Long userId, Long productId) {
    return reviewRepository.findByUserIdAndProductId(userId, productId)
      .map(reviewMapper::toDto)
      .orElseThrow(() -> new NotFound("Review not found with userId: " + userId + " and productId: " + productId));
  }

  public ReviewDto createReview(ReviewCreate reviewCreate) {
    if (reviewRepository.existsByUserIdAndProductId(reviewCreate.getUserId(), reviewCreate.getProductId())) {
      throw new ReviewAlreadyExists("Review already exists for user with id: " + reviewCreate.getUserId() + " and product with id: " + reviewCreate.getProductId());
    }
    if (reviewCreate.getRating() < 1 || reviewCreate.getRating() > 5) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
    }

    User user = userRepository.findById(reviewCreate.getUserId())
      .orElseThrow(() -> new NotFound("User not found with id: " + reviewCreate.getUserId()));

    Product product = productRepository.findById(reviewCreate.getProductId())
      .orElseThrow(() -> new NotFound("Product not found with id: " + reviewCreate.getProductId()));

    Review review = reviewMapper.toEntity(reviewCreate);
    review.setUser(user);
    review.setProduct(product);

    Review savedReview = reviewRepository.save(review);
    updateProductRating(product);
    return reviewMapper.toDto(savedReview);
  }

  public ReviewDto updateReview(ReviewCreate reviewUpdate) {
    if (reviewUpdate.getRating() < 1 || reviewUpdate.getRating() > 5) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
    }

    Review review = reviewRepository.findByUserIdAndProductId(reviewUpdate.getUserId(), reviewUpdate.getProductId())
      .orElseThrow(() -> new NotFound("Review not found with userId: " + reviewUpdate.getUserId() + " and productId: " + reviewUpdate.getProductId()));

    reviewMapper.partialUpdate(reviewUpdate, review);
    Review updatedReview = reviewRepository.save(review);
    updateProductRating(review.getProduct());
    return reviewMapper.toDto(updatedReview);
  }

  private void updateProductRating(Product product) {
    Double averageRating = reviewRepository.calculateAverageRating(product.getId());
    product.setAverageRating(averageRating != null ? averageRating : 0);
    productRepository.save(product);
  }

  public void deleteReview(Long id) {
    Review review = reviewRepository.findById(id)
      .orElseThrow(() -> new NotFound("Review not found with id: " + id));
    reviewRepository.delete(review);
  }

  // Небезпечний
  public void deleteAllReviews() {
    List<Review> reviews = reviewRepository.findAll();
    reviewRepository.deleteAll(reviews);
  }

  public void deleteReviewsByProductId(Long productId) {
    List<Review> reviews = reviewRepository.findByProductId(productId);
    reviewRepository.deleteAll(reviews);
  }
}
