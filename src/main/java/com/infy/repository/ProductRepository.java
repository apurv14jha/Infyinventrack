package com.infy.repository;

import com.infy.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
    void deleteBySku(String sku);
}
