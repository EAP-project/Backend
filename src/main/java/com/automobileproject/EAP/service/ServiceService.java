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

    /**
     * Creates a new service. (Called by ADMIN)
     */
    public Service createService(ServiceDTO serviceDTO) {
        Service service = new Service();
        service.setName(serviceDTO.getName());
        service.setDescription(serviceDTO.getDescription());
        service.setEstimatedCost(serviceDTO.getEstimatedCost());
        service.setEstimatedDurationMinutes(serviceDTO.getEstimatedDurationMinutes());

        return serviceRepository.save(service);
    }

    /**
     * Deletes a service. (Called by ADMIN)
     */
    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new EntityNotFoundException("Service not found with id: " + id);
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
     * Gets one service by its ID. (Called by CUSTOMER)
     */
    public Service getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + id));
    }
}