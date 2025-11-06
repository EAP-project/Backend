package com.automobileproject.EAP.controller;

import com.automobileproject.EAP.dto.ServiceDTO;
import com.automobileproject.EAP.model.Service;
import com.automobileproject.EAP.service.ServiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private ObjectMapper objectMapper;

    // CREATE: POST /api/services
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Service> createService(
            @RequestParam("service") String serviceDtoJson,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        try {
            // Parse JSON string to DTO
            ServiceDTO serviceDTO = objectMapper.readValue(serviceDtoJson, ServiceDTO.class);

            Service newService = serviceService.createService(serviceDTO, imageFile);
            return new ResponseEntity<>(newService, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // UPDATE: PUT /api/services/1
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Service> updateService(
            @PathVariable Long id,
            @RequestParam("service") String serviceDtoJson,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        try {
            ServiceDTO serviceDTO = objectMapper.readValue(serviceDtoJson, ServiceDTO.class);

            Service updatedService = serviceService.updateService(id, serviceDTO, imageFile);
            return ResponseEntity.ok(updatedService);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE: DELETE /api/services/1
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        try {
            serviceService.deleteService(id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // READ (Get all): GET /api/services
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<List<Service>> getAllServices() {
        List<Service> services = serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }

    // READ (Get by category): GET /api/services/category/1
    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<List<Service>> getServicesByCategory(@PathVariable Long categoryId) {
        List<Service> services = serviceService.getServicesByCategory(categoryId);
        return ResponseEntity.ok(services);
    }

    // READ (Get one): GET /api/services/1
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        Service service = serviceService.getServiceById(id);
        return ResponseEntity.ok(service);
    }
}