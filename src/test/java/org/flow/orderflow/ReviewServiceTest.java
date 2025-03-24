package org.flow.orderflow;

import org.flow.orderflow.dto.review.ReviewCreate;
import org.flow.orderflow.dto.review.ReviewDto;
import org.flow.orderflow.exception.NotFound;
import org.flow.orderflow.mapper.ReviewMapper;
import org.flow.orderflow.model.Product;
import org.flow.orderflow.model.Review;
import org.flow.orderflow.model.User;
import org.flow.orderflow.repository.ProductRepository;
import org.flow.orderflow.repository.ReviewRepository;
import org.flow.orderflow.repository.UserRepository;
import org.flow.orderflow.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private ReviewMapper reviewMapper;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private ReviewService reviewService;

  private Review review;
  private ReviewDto reviewDto;
  private ReviewCreate reviewCreate;
  private User user;
  private Product product;

  @BeforeEach
  void setUp() {
    // Initialize test data
    user = User.builder()
      .id(1L)
      .first_name("testuser")
      .build();

    product = Product.builder()
      .id(1L)
      .name("Test Product")
      .build();

    review = Review.builder()
      .id(1L)
      .rating(4)
      .content("Great product")
      .user(user)
      .product(product)
      .createdAt(LocalDateTime.now())
      .build();

    reviewDto = ReviewDto.builder()
      .id(1L)
      .rating(4)
      .content("Great product")
      .userId(1L)
      .productId(1L)
      .userFirst_name("testuser")
      .productName("Test Product")
      .build();

    reviewCreate = ReviewCreate.builder()
      .rating(4)
      .content("Great product")
      .userId(1L)
      .productId(1L)
      .build();
  }

  @Test
  void getAllReviews_shouldReturnAllReviews() {
    // Arrange
    List<Review> reviews = Collections.singletonList(review);
    List<ReviewDto> expectedDtos = Collections.singletonList(reviewDto);

    when(reviewRepository.findAll()).thenReturn(reviews);
    when(reviewMapper.toDtoList(reviews)).thenReturn(expectedDtos);

    // Act
    List<ReviewDto> result = reviewService.getAllReviews();

    // Assert
    assertThat(result).isEqualTo(expectedDtos);
    verify(reviewRepository).findAll();
    verify(reviewMapper).toDtoList(reviews);
  }

  @Test
  void getReviewById_whenReviewExists_shouldReturnReview() {
    // Arrange
    when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
    when(reviewMapper.toDto(review)).thenReturn(reviewDto);

    // Act
    ReviewDto result = reviewService.getReviewById(1L);

    // Assert
    assertThat(result).isEqualTo(reviewDto);
    verify(reviewRepository).findById(1L);
    verify(reviewMapper).toDto(review);
  }

  @Test
  void getReviewById_whenReviewDoesNotExist_shouldThrowNotFound() {
    // Arrange
    when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    NotFound exception = assertThrows(NotFound.class, () ->
      reviewService.getReviewById(999L)
    );

    assertThat(exception.getMessage()).contains("Review not found with id: 999");
    verify(reviewRepository).findById(999L);
  }

  @Test
  void getReviewsByProductId_shouldReturnReviews() {
    // Arrange
    List<Review> reviews = Collections.singletonList(review);
    List<ReviewDto> expectedDtos = Collections.singletonList(reviewDto);

    when(reviewRepository.findByProductId(1L)).thenReturn(reviews);
    when(reviewMapper.toDtoList(reviews)).thenReturn(expectedDtos);

    // Act
    List<ReviewDto> result = reviewService.getReviewsByProductId(1L);

    // Assert
    assertThat(result).isEqualTo(expectedDtos);
    verify(reviewRepository).findByProductId(1L);
    verify(reviewMapper).toDtoList(reviews);
  }

  @Test
  void getReviewsByProductName_shouldReturnReviews() {
    // Arrange
    String productName = "Test Product";
    List<Review> reviews = Collections.singletonList(review);
    List<ReviewDto> expectedDtos = Collections.singletonList(reviewDto);

    when(reviewRepository.findByProductName(productName)).thenReturn(reviews);
    when(reviewMapper.toDtoList(reviews)).thenReturn(expectedDtos);

    // Act
    List<ReviewDto> result = reviewService.getReviewsByProductName(productName);

    // Assert
    assertThat(result).isEqualTo(expectedDtos);
    verify(reviewRepository).findByProductName(productName);
    verify(reviewMapper).toDtoList(reviews);
  }

  @Test
  void getReviewsByUserId_shouldReturnReviews() {
    // Arrange
    List<Review> reviews = Collections.singletonList(review);
    List<ReviewDto> expectedDtos = Collections.singletonList(reviewDto);

    when(reviewRepository.findByUserId(1L)).thenReturn(reviews);
    when(reviewMapper.toDtoList(reviews)).thenReturn(expectedDtos);

    // Act
    List<ReviewDto> result = reviewService.getReviewsByUserId(1L);

    // Assert
    assertThat(result).isEqualTo(expectedDtos);
    verify(reviewRepository).findByUserId(1L);
    verify(reviewMapper).toDtoList(reviews);
  }

  @Test
  void getReviewByUserAndProduct_whenReviewExists_shouldReturnReview() {
    // Arrange
    when(reviewRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(Optional.of(review));
    when(reviewMapper.toDto(review)).thenReturn(reviewDto);

    // Act
    ReviewDto result = reviewService.getReviewByUserAndProduct(1L, 1L);

    // Assert
    assertThat(result).isEqualTo(reviewDto);
    verify(reviewRepository).findByUserIdAndProductId(1L, 1L);
    verify(reviewMapper).toDto(review);
  }

  @Test
  void getReviewByUserAndProduct_whenReviewDoesNotExist_shouldThrowNotFound() {
    // Arrange
    when(reviewRepository.findByUserIdAndProductId(999L, 999L)).thenReturn(Optional.empty());

    // Act & Assert
    NotFound exception = assertThrows(NotFound.class, () ->
      reviewService.getReviewByUserAndProduct(999L, 999L)
    );

    assertThat(exception.getMessage()).contains("Review not found with userId: 999 and productId: 999");
    verify(reviewRepository).findByUserIdAndProductId(999L, 999L);
  }

  @Test
  void createReview_shouldCreateAndReturnReview() {
    // Arrange
    when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(reviewMapper.toEntity(reviewCreate)).thenReturn(review);
    when(reviewRepository.save(any(Review.class))).thenReturn(review);
    when(reviewMapper.toDto(review)).thenReturn(reviewDto);

    // Act
    ReviewDto result = reviewService.createReview(reviewCreate);

    // Assert
    assertThat(result).isEqualTo(reviewDto);
    verify(reviewRepository).existsByUserIdAndProductId(1L, 1L);
    verify(userRepository).findById(1L);
    verify(productRepository).findById(1L);
    verify(reviewMapper).toEntity(reviewCreate);
    verify(reviewRepository).save(any(Review.class));
    verify(reviewMapper).toDto(review);
  }

  @Test
  void createReview_whenReviewAlreadyExists_shouldThrowException() {
    // Arrange
    when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(true);

    // Act & Assert
    Exception exception = assertThrows(RuntimeException.class, () ->
      reviewService.createReview(reviewCreate)
    );

    assertThat(exception.getMessage()).contains("Review already exists for user with id: 1 and product with id: 1");
    verify(reviewRepository).existsByUserIdAndProductId(1L, 1L);
    verifyNoInteractions(userRepository, productRepository, reviewMapper);
  }

  @Test
  void createReview_whenRatingIsInvalid_shouldThrowException() {
    // Arrange
    reviewCreate.setRating(6);

    // Act & Assert
    HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
      reviewService.createReview(reviewCreate)
    );

    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(exception.getMessage()).contains("Rating must be between 1 and 5");
  }

  @Test
  void updateReview_shouldUpdateAndReturnReview() {
    // Arrange
    when(reviewRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(Optional.of(review));
    when(reviewRepository.save(any(Review.class))).thenReturn(review);
    when(reviewMapper.toDto(review)).thenReturn(reviewDto);

    // Act
    ReviewDto result = reviewService.updateReview(reviewCreate);

    // Assert
    assertThat(result).isEqualTo(reviewDto);
    verify(reviewRepository).findByUserIdAndProductId(1L, 1L);
    verify(reviewMapper).partialUpdate(reviewCreate, review);
    verify(reviewRepository).save(any(Review.class));
    verify(reviewMapper).toDto(review);
  }

  @Test
  void deleteReview_whenReviewExists_shouldDeleteReview() {
    // Arrange
    when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

    // Act
    reviewService.deleteReview(1L);

    // Assert
    verify(reviewRepository).findById(1L);
    verify(reviewRepository).delete(review);
  }

  @Test
  void deleteReview_whenReviewDoesNotExist_shouldThrowNotFound() {
    // Arrange
    when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    NotFound exception = assertThrows(NotFound.class, () ->
      reviewService.deleteReview(999L)
    );

    assertThat(exception.getMessage()).contains("Review not found with id: 999");
    verify(reviewRepository).findById(999L);
    verify(reviewRepository, never()).delete(any());
  }

  @Test
  void deleteAllReviews_shouldDeleteAllReviews() {
    // Arrange
    List<Review> reviews = Collections.singletonList(review);
    when(reviewRepository.findAll()).thenReturn(reviews);

    // Act
    reviewService.deleteAllReviews();

    // Assert
    verify(reviewRepository).findAll();
    verify(reviewRepository).deleteAll(reviews);
  }

  @Test
  void deleteReviewsByProductId_shouldDeleteReviews() {
    // Arrange
    List<Review> reviews = Collections.singletonList(review);
    when(reviewRepository.findByProductId(1L)).thenReturn(reviews);

    // Act
    reviewService.deleteReviewsByProductId(1L);

    // Assert
    verify(reviewRepository).findByProductId(1L);
    verify(reviewRepository).deleteAll(reviews);
  }
}
