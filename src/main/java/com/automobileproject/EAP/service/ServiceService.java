package com.automobileproject.EAP.service;

import com.automobileproject.EAP.dto.ServiceDTO;
import com.automobileproject.EAP.model.Service;
import com.automobileproject.EAP.model.ServiceCategory;
import com.automobileproject.EAP.repository.ServiceCategoryRepository;
import com.automobileproject.EAP.repository.ServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceCategoryRepository categoryRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    /**
     * Creates a new service with image upload. (Called by ADMIN)
     */
    public Service createService(ServiceDTO serviceDTO, MultipartFile imageFile) throws IOException {
        // Find the category
        ServiceCategory category = categoryRepository.findById(serviceDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + serviceDTO.getCategoryId()));

        // Upload image to Cloudinary
        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = cloudinaryService.uploadImage(imageFile, "services");
        }

        Service service = Service.builder()
                .name(serviceDTO.getName())
                .description(serviceDTO.getDescription())
                .estimatedCost(serviceDTO.getEstimatedCost())
                .estimatedDurationMinutes(serviceDTO.getEstimatedDurationMinutes())
                .imageUrl(imageUrl)
                .category(category)
                .build();

        return serviceRepository.save(service);
    }

    /**
     * Updates an existing service with optional image upload. (Called by ADMIN)
     */
    public Service updateService(Long id, ServiceDTO serviceDTO, MultipartFile imageFile) throws IOException {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + id));

        // Find the category
        ServiceCategory category = categoryRepository.findById(serviceDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + serviceDTO.getCategoryId()));

        // Update image if new file provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String oldImageUrl = service.getImageUrl();
            String newImageUrl = cloudinaryService.updateImage(imageFile, oldImageUrl, "services");
            service.setImageUrl(newImageUrl);
        }

        service.setName(serviceDTO.getName());
        service.setDescription(serviceDTO.getDescription());
        service.setEstimatedCost(serviceDTO.getEstimatedCost());
        service.setEstimatedDurationMinutes(serviceDTO.getEstimatedDurationMinutes());
        service.setCategory(category);

        return serviceRepository.save(service);
    }

    /**
     * Deletes a service and its image. (Called by ADMIN)
     */
    public void deleteService(Long id) throws IOException {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + id));

        // Delete image from Cloudinary
        if (service.getImageUrl() != null) {
            try {
                cloudinaryService.deleteImage(service.getImageUrl());
            } catch (Exception e) {
                // Log but don't fail if image deletion fails
                System.err.println("Failed to delete service image: " + e.getMessage());
            }
        }

        serviceRepository.deleteById(id);
    }

    /**
     * Gets a list of all services. (Called by CUSTOMER)
     */
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    /**
     * Gets services by category. (Called by CUSTOMER)
     */
    public List<Service> getServicesByCategory(Long categoryId) {
        return serviceRepository.findByCategoryId(categoryId);
    }

    /**
     * Gets one service by its ID. (Called by CUSTOMER)
     */
    public Service getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + id));
    }
}