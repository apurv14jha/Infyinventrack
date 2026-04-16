package com.infy.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductRequestDto {

    @NotBlank(message = "Please provide a valid productName")
    @Size(min = 3, max = 100)
    private String productName;

    @NotBlank(message = "Please provide a valid sku")
    @Size(min = 3, max = 20)
    private String sku;

    @NotNull(message = "Please provide a valid price")
    @DecimalMin(value = "0.01", message = "Please provide a valid price")
    private Double price;

    @NotBlank(message = "Please provide a valid imageUrl")
    private String imageUrl;

    @NotBlank(message = "Please provide a valid category")
    private String category;

    @Size(max = 500)
    private String description;

    @NotBlank(message = "Please provide a valid brand")
    @Size(min = 2, max = 50)
    private String brand;

    @NotNull(message = "Please provide a valid stockQuantity")
    @DecimalMin(value = "0", message = "Please provide a valid stockQuantity")
    private Integer stockQuantity;

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
}
