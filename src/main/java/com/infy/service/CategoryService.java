package com.infy.service;

import com.infy.dto.CategoryDto;
import com.infy.enums.UserRole;

public interface CategoryService {
    String createCategory(CategoryDto dto, UserRole role);
    String updateCategory(Integer id, CategoryDto dto, UserRole role);
    String deleteCategory(Integer id, UserRole role);
}
