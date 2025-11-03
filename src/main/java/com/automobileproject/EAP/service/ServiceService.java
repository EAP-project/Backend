package com.automobileproject.EAP.service;

import com.automobileproject.EAP.dto.ServiceDTO;
import com.automobileproject.EAP.model.Service;
import com.automobileproject.EAP.repository.ServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    // CREATE
    public Service createService(ServiceDTO serviceDTO) {
        Service service = new Service();
        service.setName(serviceDTO.getName());
        service.setDescription(serviceDTO.getDescription());
        service.setEstimatedCost(serviceDTO.getEstimatedCost());
        service.setEstimatedDurationMinutes(serviceDTO.getEstimatedDurationMinutes());

        return serviceRepository.save(service);
    }

    // READ (Get one by ID)
    public Service getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + id));
    }

    // READ (Get all)
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    // UPDATE
    public Service updateService(Long id, ServiceDTO serviceDTO) {
        // First, check if the service exists
        Service existingService = getServiceById(id);

        // Update the fields
        existingService.setName(serviceDTO.getName());
        existingService.setDescription(serviceDTO.getDescription());
        existingService.setEstimatedCost(serviceDTO.getEstimatedCost());
        existingService.setEstimatedDurationMinutes(serviceDTO.getEstimatedDurationMinutes());

        return serviceRepository.save(existingService);
    }

    // DELETE
    public void deleteService(Long id) {
        // Check if the service exists before deleting
        if (!serviceRepository.existsById(id)) {
            throw new EntityNotFoundException("Service not found with id: " + id);
        }
        serviceRepository.deleteById(id);
    }
}