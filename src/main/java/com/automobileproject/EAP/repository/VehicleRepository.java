package com.automobileproject.EAP.repository;

import com.automobileproject.EAP.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // Make sure this is imported if you use it

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // --- THIS IS THE MISSING LINE ---
    // Your VehicleService needs this method for the GET request.
    List<Vehicle> findByOwnerId(Long ownerId);
    // ---------------------------------

}