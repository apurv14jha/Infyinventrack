package com.infy.controller;

import com.infy.dto.CategoryDto;
import com.infy.enums.UserRole;
import com.infy.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/infyinventrack/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody CategoryDto dto, @RequestParam UserRole role) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(dto, role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Integer id, @Valid @RequestBody CategoryDto dto, @RequestParam UserRole role) {
        return ResponseEntity.ok(categoryService.updateCategory(id, dto, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id, @RequestParam UserRole role) {
        return ResponseEntity.ok(categoryService.deleteCategory(id, role));
    }
}
