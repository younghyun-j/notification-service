package com.eccomerce.api.product.repository;

import com.eccomerce.api.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
