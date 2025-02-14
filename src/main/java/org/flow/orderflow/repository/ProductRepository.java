package org.flow.orderflow.repository;

import org.flow.orderflow.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

  Product findByName(String name);

  List<Product> findByCategoryId(Long categoryId);

  @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(concat('%', :query, '%'))")
  List<Product> searchByNameContaining(@Param("query") String query, Pageable pageable);

  @Query("SELECT p FROM Product p WHERE " +
    "(:searchTerm IS NULL OR LOWER(p.name) LIKE LOWER(concat('%', :searchTerm, '%'))) AND " +
    "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
    "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
    "(:inStock IS NULL OR (:inStock = true AND p.stock > 0) OR (:inStock = false AND p.stock <= 0))")
  Page<Product> filterSortAndSearchProducts(
    @Param("searchTerm") String searchTerm,
    @Param("minPrice") Double minPrice,
    @Param("maxPrice") Double maxPrice,
    @Param("inStock") Boolean inStock,
    Pageable pageable
  );

  @Override
  Page<Product> findAll(Pageable pageable);
}
