package com.infy.enums;

public enum InfyInvenTrackConstants {
    INVALID_ATTRIBUTE("Please provide a valid %s"),
    PRODUCT_CREATED("201 - Successfully created the product"),
    PRODUCT_UPDATED("200 - Update successfully completed with updated product details."),
    PRODUCT_DELETED("200 - Deleted the product"),
    SKU_CONFLICT("409 - Conflict with existing SKU"),
    PRODUCT_NOT_FOUND("404 - no product found"),
    CATEGORY_CREATED("201 - Category created successfully"),
    CATEGORY_UPDATED("200 - Category updated successfully"),
    CATEGORY_DELETED("200 - Category deleted successfully"),
    INSUFFICIENT_STOCK("409 - insufficient stock"),
    UNAUTHORIZED("User is not authorized for this operation");

    private final String value;

    InfyInvenTrackConstants(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
