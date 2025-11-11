package com.automobileproject.EAP.service;

import com.automobileproject.EAP.dto.VehicleDTO;
import com.automobileproject.EAP.model.User;
import com.automobileproject.EAP.model.Vehicle;
import com.automobileproject.EAP.repository.UserRepository;
import com.automobileproject.EAP.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    /**
     * Creates a new vehicle and assigns it to the customer.
     *
     * @param vehicleDTO The vehicle data from the request.
     * @param email The email of the logged-in customer (from the JWT).
     */
    public Vehicle createVehicle(VehicleDTO vehicleDTO, String email) {

        // 1. Find the User by their EMAIL
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 2. Build the new Vehicle entity
        Vehicle vehicle = Vehicle.builder()
                .model(vehicleDTO.getModel())
                .year(vehicleDTO.getYear())
                .licensePlate(vehicleDTO.getLicensePlate())
                .owner(owner) // 3. Set the owner
                .build();

        // 4. Save to the database
        return vehicleRepository.save(vehicle);
    }

    /**
     * Retrieves all vehicles owned by the logged-in customer.
     *
     * @param email The email of the logged-in customer (from the JWT).
     */
    public List<Vehicle> getVehiclesByOwner(String email) {

        // 1. Find the User by their EMAIL
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 2. Use the repository method to find all vehicles by that user's ID
        return vehicleRepository.findByOwnerId(owner.getId());
    }
}