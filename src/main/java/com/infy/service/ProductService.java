package com.infy.service;

import com.infy.dto.ProductRequestDto;
import com.infy.dto.ProductResponseDto;
import com.infy.enums.UserRole;

import java.util.List;

public interface ProductService {
    String createProduct(ProductRequestDto productRequest, Integer userId, UserRole role);
    ProductResponseDto getProductBySku(String sku, Integer userId, UserRole role);
    String updateProduct(String sku, ProductRequestDto productRequest, Integer userId, UserRole role);
    String deleteProduct(String sku, Integer userId, UserRole role);
    List<ProductResponseDto> searchProducts(String name, String category, Double minPrice, Double maxPrice, Integer page, Integer size, String sortBy, UserRole role);
    ProductResponseDto viewInventory(String sku, UserRole role);
    String reserveStock(String sku, Integer quantity, Integer userId, UserRole role);
    Double calculateTotalInventoryValue(UserRole role);
    List<ProductResponseDto> identifyLowStock(Integer threshold, UserRole role);
    List<ProductResponseDto> findTopExpensive(Integer n, UserRole role);
    String updateDescriptionWithCacheBust(String sku, String description, UserRole role);
}
