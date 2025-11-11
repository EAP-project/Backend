package com.automobileproject.EAP.controller;

import com.automobileproject.EAP.dto.VehicleDTO;
import com.automobileproject.EAP.model.Vehicle;
import com.automobileproject.EAP.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     * Endpoint for a customer to add a new vehicle.
     */
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Vehicle> createVehicle(
            @Valid @RequestBody VehicleDTO vehicleDTO,
            Authentication authentication // Injects the logged-in user's data
    ) {
        // authentication.getName() will return the email (e.g., "john@example.com")
        String userEmail = authentication.getName();

        Vehicle newVehicle = vehicleService.createVehicle(vehicleDTO, userEmail);
        return new ResponseEntity<>(newVehicle, HttpStatus.CREATED);
    }

    /**
     * Endpoint for a customer to get a list of THEIR OWN vehicles.
     */
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Vehicle>> getMyVehicles(Authentication authentication) {

        // authentication.getName() will return the email
        String userEmail = authentication.getName();

        List<Vehicle> vehicles = vehicleService.getVehiclesByOwner(userEmail);
        return ResponseEntity.ok(vehicles);
    }
}