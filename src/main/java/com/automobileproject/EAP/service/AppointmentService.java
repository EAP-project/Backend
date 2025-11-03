package com.automobileproject.EAP.service;

import com.automobileproject.EAP.dto.AppointmentRequestDTO;
import com.automobileproject.EAP.model.Appointment;
import com.automobileproject.EAP.model.User;
import com.automobileproject.EAP.model.Vehicle;
import com.automobileproject.EAP.repository.AppointmentRepository;
import com.automobileproject.EAP.repository.ServiceRepository;
import com.automobileproject.EAP.repository.UserRepository;
import com.automobileproject.EAP.repository.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRepository serviceRepository;

    // Define which statuses mean a car is "In the Garage"
    private static final List<Appointment.AppointmentStatus> ACTIVE_STATUSES = Arrays.asList(
            Appointment.AppointmentStatus.IN_PROGRESS,
            Appointment.AppointmentStatus.AWAITING_PARTS
    );

    public Appointment createStandardAppointment(AppointmentRequestDTO dto, String customerEmail) {

        // 1. Find the logged-in customer
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + customerEmail));

        // 2. Find the vehicle and service from the request
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));

        com.automobileproject.EAP.model.Service service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));

        // 3. --- CRITICAL SECURITY CHECK ---
        // Ensure the vehicle belongs to the logged-in customer
        if (!vehicle.getOwner().getId().equals(customer.getId())) {
            throw new AccessDeniedException("You do not have permission to book an appointment for this vehicle.");
        }

        // 4. --- CRITICAL LOGIC CHECK ---
        // Check if the vehicle is already in the garage for another job
        boolean isVehicleBusy = appointmentRepository.existsByVehicleIdAndStatusIn(vehicle.getId(), ACTIVE_STATUSES);
        if (isVehicleBusy) {
            throw new IllegalStateException("This vehicle is already in the garage for another service.");
        }

        // 5. Build the new appointment
        Appointment appointment = Appointment.builder()
                .vehicle(vehicle)
                .service(service)
                .appointmentDateTime(dto.getAppointmentDateTime())
                .customerNotes(dto.getCustomerNotes())
                .appointmentType(Appointment.AppointmentType.STANDARD_SERVICE)
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .build();

        // 6. Save and return
        return appointmentRepository.save(appointment);
    }
}
