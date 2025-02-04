package org.flow.orderflow.repository;

import org.flow.orderflow.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
