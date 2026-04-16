package com.infy.service.impl;

import com.infy.dto.CategoryDto;
import com.infy.entity.Category;
import com.infy.enums.InfyInvenTrackConstants;
import com.infy.enums.UserRole;
import com.infy.exception.BadRequestException;
import com.infy.exception.ConflictException;
import com.infy.exception.NotFoundException;
import com.infy.repository.CategoryRepository;
import com.infy.repository.ProductRepository;
import com.infy.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public String createCategory(CategoryDto dto, UserRole role) {
        ensureAdmin(role);
        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new ConflictException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "name"));
        }
        Category category = new Category();
        category.setName(dto.getName());
        categoryRepository.save(category);
        LOGGER.info("Category created id={}", category.getId());
        return InfyInvenTrackConstants.CATEGORY_CREATED.value();
    }

    @Override
    public String updateCategory(Integer id, CategoryDto dto, UserRole role) {
        ensureAdmin(role);
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "id")));
        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new ConflictException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "name"));
        }
        category.setName(dto.getName());
        categoryRepository.save(category);
        LOGGER.info("Category updated id={}", id);
        return InfyInvenTrackConstants.CATEGORY_UPDATED.value();
    }

    @Override
    public String deleteCategory(Integer id, UserRole role) {
        ensureAdmin(role);
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "id")));

        boolean inUse = productRepository.findAll().stream()
            .anyMatch(product -> product.getCategory().getId().equals(id));
        if (inUse) {
            throw new ConflictException("409 - category is in use");
        }
        categoryRepository.delete(category);
        LOGGER.info("Category deleted id={}", id);
        return InfyInvenTrackConstants.CATEGORY_DELETED.value();
    }

    private void ensureAdmin(UserRole role) {
        if (role != UserRole.ADMIN) {
            throw new BadRequestException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "role"));
        }
    }
}
