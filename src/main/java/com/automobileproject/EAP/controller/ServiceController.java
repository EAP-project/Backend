package com.automobileproject.EAP.controller;

import com.automobileproject.EAP.dto.ServiceDTO;
import com.automobileproject.EAP.model.Service;
import com.automobileproject.EAP.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services") // Base URL for all service-related APIs
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    // CREATE: POST http://localhost:8080/api/services
    @PostMapping
    public ResponseEntity<Service> createService(@Valid @RequestBody ServiceDTO serviceDTO) {
        Service newService = serviceService.createService(serviceDTO);
        return new ResponseEntity<>(newService, HttpStatus.CREATED);
    }

    // READ (Get all): GET http://localhost:8080/api/services
    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() {
        List<Service> services = serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }

    // READ (Get one): GET http://localhost:8080/api/services/1
    @GetMapping("/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        Service service = serviceService.getServiceById(id);
        return ResponseEntity.ok(service);
    }

    // UPDATE: PUT http://localhost:8080/api/services/1
    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable Long id, @Valid @RequestBody ServiceDTO serviceDTO) {
        Service updatedService = serviceService.updateService(id, serviceDTO);
        return ResponseEntity.ok(updatedService);
    }

    // DELETE: DELETE http://localhost:8080/api/services/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build(); // Standard response for a successful delete
    }
}