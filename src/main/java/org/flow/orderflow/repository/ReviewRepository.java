package org.flow.orderflow.repository;

import org.flow.orderflow.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
  List<Review> findByProductId(Long productId);

  List<Review> findByProductName(String productName);

  List<Review> findByUserId(Long userId);

  Optional<Review> findByUserIdAndProductId(Long userId, Long productId);

  boolean existsByUserIdAndProductId(Long userId, Long productId);

  @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :id")
  Double calculateAverageRating(Long id);
}
