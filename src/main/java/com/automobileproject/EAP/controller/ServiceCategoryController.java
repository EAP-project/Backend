package com.automobileproject.EAP.controller;

import com.automobileproject.EAP.dto.ServiceCategoryDTO;
import com.automobileproject.EAP.model.ServiceCategory;
import com.automobileproject.EAP.service.ServiceCategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-categories")
public class ServiceCategoryController {

    @Autowired
    private ServiceCategoryService categoryService;

    private static final Logger logger = LoggerFactory.getLogger(ServiceCategoryController.class);

    // CREATE: POST /api/service-categories
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceCategory> createCategory(@Valid @RequestBody ServiceCategoryDTO categoryDTO) {
        logger.info("CREATE CATEGORY ENDPOINT HIT - Category: {}", categoryDTO.getName());
        ServiceCategory newCategory = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    // UPDATE: PUT /api/service-categories/1
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceCategory> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody ServiceCategoryDTO categoryDTO) {
        ServiceCategory updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    // DELETE: DELETE /api/service-categories/1
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // READ (Get all): GET /api/service-categories
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<List<ServiceCategory>> getAllCategories() {
        List<ServiceCategory> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // READ (Get one): GET /api/service-categories/1
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<ServiceCategory> getCategoryById(@PathVariable Long id) {
        ServiceCategory category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }
}