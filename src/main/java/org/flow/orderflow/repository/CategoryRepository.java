package org.flow.orderflow.repository;

import org.flow.orderflow.model.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  Category findByName(String name);

  @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(concat('%', :query, '%'))")
  List<Category> searchByNameContaining(@Param("query") String query, Pageable pageable);

}
