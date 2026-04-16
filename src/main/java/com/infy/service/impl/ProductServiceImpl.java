package com.infy.service.impl;

import com.infy.dto.ProductRequestDto;
import com.infy.dto.ProductResponseDto;
import com.infy.entity.Category;
import com.infy.entity.Product;
import com.infy.enums.InfyInvenTrackConstants;
import com.infy.enums.UserRole;
import com.infy.exception.BadRequestException;
import com.infy.exception.ConflictException;
import com.infy.exception.NotFoundException;
import com.infy.repository.CategoryRepository;
import com.infy.repository.ProductRepository;
import com.infy.service.ProductService;
import com.infy.util.RoleValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final RoleValidator roleValidator;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, RoleValidator roleValidator) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.roleValidator = roleValidator;
    }

    @Override
    public String createProduct(ProductRequestDto productRequest, Integer userId, UserRole role) {
        roleValidator.validateRole(userId, role, Set.of(UserRole.ADMIN));
        validateUrl(productRequest.getImageUrl());

        if (productRepository.existsBySku(productRequest.getSku())) {
            throw new ConflictException(InfyInvenTrackConstants.SKU_CONFLICT.value());
        }

        Category category = categoryRepository.findByNameIgnoreCase(productRequest.getCategory())
            .orElseThrow(() -> new BadRequestException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "category")));

        Product product = mapToEntity(productRequest, category);
        productRepository.save(product);
        LOGGER.info("Product created at timestamp={} by userId={} for sku={}", System.currentTimeMillis(), userId, product.getSku());
        return InfyInvenTrackConstants.PRODUCT_CREATED.value();
    }

    @Override
    @Cacheable(cacheNames = "productsBySku", key = "#sku")
    public ProductResponseDto getProductBySku(String sku, Integer userId, UserRole role) {
        roleValidator.validateRole(userId, role, Set.of(UserRole.ADMIN));
        Product product = productRepository.findBySku(sku)
            .orElseThrow(() -> new NotFoundException(InfyInvenTrackConstants.PRODUCT_NOT_FOUND.value()));
        LOGGER.info("Product retrieved for sku={} by userId={}", sku, userId);
        return mapToResponse(product);
    }

    @Override
    @CacheEvict(cacheNames = "productsBySku", key = "#sku")
    public String updateProduct(String sku, ProductRequestDto productRequest, Integer userId, UserRole role) {
        roleValidator.validateRole(userId, role, Set.of(UserRole.ADMIN));
        Product product = productRepository.findBySku(sku)
            .orElseThrow(() -> new NotFoundException(InfyInvenTrackConstants.PRODUCT_NOT_FOUND.value()));

        validateUrl(productRequest.getImageUrl());
        Category category = categoryRepository.findByNameIgnoreCase(productRequest.getCategory())
            .orElseThrow(() -> new BadRequestException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "category")));

        product.setProductName(productRequest.getProductName());
        product.setPrice(productRequest.getPrice());
        product.setImageUrl(productRequest.getImageUrl());
        product.setCategory(category);
        product.setDescription(productRequest.getDescription());
        product.setBrand(productRequest.getBrand());
        product.setStockQuantity(productRequest.getStockQuantity());

        productRepository.save(product);
        LOGGER.info("Product updated for sku={} by userId={}", sku, userId);
        return InfyInvenTrackConstants.PRODUCT_UPDATED.value();
    }

    @Override
    @CacheEvict(cacheNames = "productsBySku", key = "#sku")
    public String deleteProduct(String sku, Integer userId, UserRole role) {
        roleValidator.validateRole(userId, role, Set.of(UserRole.ADMIN));
        Product product = productRepository.findBySku(sku)
            .orElseThrow(() -> new NotFoundException(InfyInvenTrackConstants.PRODUCT_NOT_FOUND.value()));
        productRepository.delete(product);
        LOGGER.info("Product deleted for sku={} by userId={}", sku, userId);
        return InfyInvenTrackConstants.PRODUCT_DELETED.value();
    }

    @Override
    public List<ProductResponseDto> searchProducts(String name, String category, Double minPrice, Double maxPrice, Integer page, Integer size, String sortBy, UserRole role) {
        if (!(role == UserRole.ADMIN || role == UserRole.CUSTOMER)) {
            throw new BadRequestException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "role"));
        }
        validatePageSize(page, size);
        List<String> sortable = List.of("productName", "price", "brand", "stockQuantity", "category");
        if (!sortable.contains(sortBy)) {
            throw new BadRequestException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "sortBy"));
        }

        List<Product> products = productRepository.findAll(PageRequest.of(page, size, Sort.by("productName"))).getContent();

        Predicate<Product> byName = p -> name == null || p.getProductName().toLowerCase().contains(name.toLowerCase());
        Predicate<Product> byCategory = p -> category == null || p.getCategory().getName().equalsIgnoreCase(category);
        Predicate<Product> byPrice = p -> (minPrice == null || p.getPrice() >= minPrice) && (maxPrice == null || p.getPrice() <= maxPrice);

        Comparator<Product> comparator = switch (sortBy) {
            case "price" -> Comparator.comparing(Product::getPrice);
            case "brand" -> Comparator.comparing(Product::getBrand);
            case "stockQuantity" -> Comparator.comparing(Product::getStockQuantity);
            case "category" -> Comparator.comparing(p -> p.getCategory().getName());
            default -> Comparator.comparing(Product::getProductName);
        };

        return products.stream()
            .filter(byName.and(byCategory).and(byPrice))
            .sorted(comparator)
            .limit(5)
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    public ProductResponseDto viewInventory(String sku, UserRole role) {
        if (role != UserRole.ADMIN) {
            throw new BadRequestException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "role"));
        }
        ProductResponseDto dto = mapToResponse(productRepository.findBySku(sku)
            .orElseThrow(() -> new NotFoundException(InfyInvenTrackConstants.PRODUCT_NOT_FOUND.value())));
        if (dto.getStockQuantity() < 5) {
            dto.setStockStatus("Low Stock");
        }
        return dto;
    }

    @Override
    public synchronized String reserveStock(String sku, Integer quantity, Integer userId, UserRole role) {
        roleValidator.validateRole(userId, role, Set.of(UserRole.CUSTOMER));
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "quantity"));
        }

        Product product = productRepository.findBySku(sku)
            .orElseThrow(() -> new NotFoundException(InfyInvenTrackConstants.PRODUCT_NOT_FOUND.value()));

        if (product.getStockQuantity() < quantity) {
            throw new ConflictException(InfyInvenTrackConstants.INSUFFICIENT_STOCK.value());
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
        return "200 - stock with " + quantity + " is reserved";
    }

    @Override
    public Double calculateTotalInventoryValue(UserRole role) {
        if (role != UserRole.ADMIN) {
            throw new BadRequestException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "role"));
        }

        return productRepository.findAll().stream()
            .mapToDouble(product -> product.getPrice() * product.getStockQuantity())
            .sum();
    }

    @Override
    public List<ProductResponseDto> identifyLowStock(Integer threshold, UserRole role) {
        if (role != UserRole.ADMIN) {
            throw new BadRequestException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "role"));
        }
        if (threshold == null || threshold <= 0) {
            throw new BadRequestException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "threshold"));
        }

        return productRepository.findAll().stream()
            .filter(product -> product.getStockQuantity() < threshold)
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    public List<ProductResponseDto> findTopExpensive(Integer n, UserRole role) {
        if (!(role == UserRole.ADMIN || role == UserRole.CUSTOMER)) {
            throw new BadRequestException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "role"));
        }
        if (n == null || n <= 0) {
            throw new BadRequestException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "n"));
        }

        return productRepository.findAll().stream()
            .sorted(Comparator.comparing(Product::getPrice).reversed())
            .limit(n)
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    @CacheEvict(cacheNames = "productsBySku", key = "#sku")
    public String updateDescriptionWithCacheBust(String sku, String description, UserRole role) {
        if (role != UserRole.ADMIN) {
            throw new BadRequestException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "role"));
        }
        if (description == null || description.isBlank()) {
            throw new BadRequestException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "description"));
        }
        Product product = productRepository.findBySku(sku)
            .orElseThrow(() -> new NotFoundException(InfyInvenTrackConstants.PRODUCT_NOT_FOUND.value()));

        product.setDescription(description);
        productRepository.save(product);
        return "200 - description updated and cache invalidated";
    }

    private Product mapToEntity(ProductRequestDto dto, Category category) {
        Product product = new Product();
        product.setProductName(dto.getProductName());
        product.setSku(dto.getSku());
        product.setPrice(dto.getPrice());
        product.setImageUrl(dto.getImageUrl());
        product.setCategory(category);
        product.setDescription(dto.getDescription());
        product.setBrand(dto.getBrand());
        product.setStockQuantity(dto.getStockQuantity());
        return product;
    }

    private ProductResponseDto mapToResponse(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setProductName(product.getProductName());
        dto.setSku(product.getSku());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setCategory(Optional.ofNullable(product.getCategory()).map(Category::getName).orElse(""));
        dto.setDescription(product.getDescription());
        dto.setBrand(product.getBrand());
        dto.setStockQuantity(product.getStockQuantity());
        return dto;
    }

    private void validateUrl(String url) {
        try {
            URI.create(url).toURL();
        } catch (Exception ex) {
            throw new BadRequestException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "imageUrl"));
        }
    }

    private void validatePageSize(Integer page, Integer size) {
        if (page == null || page < 0) {
            throw new BadRequestException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "page"));
        }
        if (size == null || size <= 0) {
            throw new BadRequestException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "size"));
        }
    }
}
