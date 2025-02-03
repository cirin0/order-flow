package org.flow.orderflow.repository;

import org.flow.orderflow.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
