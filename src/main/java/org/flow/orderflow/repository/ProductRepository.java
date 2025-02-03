package org.flow.orderflow.repository;

import org.flow.orderflow.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
