package com.automobileproject.EAP.service;

import com.automobileproject.EAP.dto.ServiceCategoryDTO;
import com.automobileproject.EAP.model.ServiceCategory;
import com.automobileproject.EAP.repository.ServiceCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCategoryService {

    @Autowired
    private ServiceCategoryRepository categoryRepository;

    /**
     * Creates a new service category. (Called by ADMIN)
     */
    public ServiceCategory createCategory(ServiceCategoryDTO categoryDTO) {
        ServiceCategory category = ServiceCategory.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .build();

        return categoryRepository.save(category);
    }

    /**
     * Updates an existing service category. (Called by ADMIN)
     */
    public ServiceCategory updateCategory(Long id, ServiceCategoryDTO categoryDTO) {
        ServiceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        return categoryRepository.save(category);
    }

    /**
     * Deletes a service category. (Called by ADMIN)
     */
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    /**
     * Gets all service categories.
     */
    public List<ServiceCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Gets one category by its ID.
     */
    public ServiceCategory getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }
}