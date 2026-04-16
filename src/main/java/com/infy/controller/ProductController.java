package com.infy.controller;

import com.infy.dto.ProductRequestDto;
import com.infy.dto.ProductResponseDto;
import com.infy.enums.UserRole;
import com.infy.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/infyinventrack/api/products")
@Validated
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<String> createProduct(@Valid @RequestBody ProductRequestDto productRequest,
                                                @RequestParam Integer userId,
                                                @RequestParam UserRole role) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productRequest, userId, role));
    }

    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponseDto> getBySku(@PathVariable @NotBlank String sku,
                                                       @RequestParam Integer userId,
                                                       @RequestParam UserRole role) {
        return ResponseEntity.ok(productService.getProductBySku(sku, userId, role));
    }

    @PutMapping("/{sku}")
    public ResponseEntity<String> updateProduct(@PathVariable @NotBlank String sku,
                                                @Valid @RequestBody ProductRequestDto productRequest,
                                                @RequestParam Integer userId,
                                                @RequestParam UserRole role) {
        return ResponseEntity.ok(productService.updateProduct(sku, productRequest, userId, role));
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<String> deleteProduct(@PathVariable @NotBlank String sku,
                                                @RequestParam Integer userId,
                                                @RequestParam UserRole role) {
        return ResponseEntity.ok(productService.deleteProduct(sku, userId, role));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProducts(@RequestParam(required = false) String name,
                                                                   @RequestParam(required = false) String category,
                                                                   @RequestParam(required = false) Double minPrice,
                                                                   @RequestParam(required = false) Double maxPrice,
                                                                   @RequestParam(defaultValue = "0") Integer page,
                                                                   @RequestParam(defaultValue = "5") Integer size,
                                                                   @RequestParam(defaultValue = "category") String sortBy,
                                                                   @RequestParam UserRole role) {
        return ResponseEntity.ok(productService.searchProducts(name, category, minPrice, maxPrice, page, size, sortBy, role));
    }

    @GetMapping("/{sku}/inventory")
    public ResponseEntity<ProductResponseDto> viewInventory(@PathVariable String sku,
                                                            @RequestParam UserRole role) {
        return ResponseEntity.ok(productService.viewInventory(sku, role));
    }

    @PostMapping("/{sku}/reserve")
    public ResponseEntity<String> reserveStock(@PathVariable String sku,
                                               @RequestParam @Min(1) Integer quantity,
                                               @RequestParam Integer userId,
                                               @RequestParam UserRole role) {
        return ResponseEntity.ok(productService.reserveStock(sku, quantity, userId, role));
    }

    @GetMapping("/inventory/value")
    public ResponseEntity<Double> calculateInventoryValue(@RequestParam UserRole role) {
        return ResponseEntity.ok(productService.calculateTotalInventoryValue(role));
    }

    @GetMapping("/lowstock")
    public ResponseEntity<List<ProductResponseDto>> lowStock(@RequestParam @Min(1) Integer threshold,
                                                             @RequestParam UserRole role) {
        return ResponseEntity.ok(productService.identifyLowStock(threshold, role));
    }

    @GetMapping("/topexpensive")
    public ResponseEntity<List<ProductResponseDto>> topExpensive(@RequestParam("n") @Min(1) Integer n,
                                                                 @RequestParam UserRole role) {
        return ResponseEntity.ok(productService.findTopExpensive(n, role));
    }

    @PutMapping("/{sku}/description")
    public ResponseEntity<String> updateDescription(@PathVariable String sku,
                                                    @RequestParam String description,
                                                    @RequestParam UserRole role) {
        return ResponseEntity.ok(productService.updateDescriptionWithCacheBust(sku, description, role));
    }
}
