package com.automobileproject.EAP.service;

import com.automobileproject.EAP.dto.AppointmentRequestDTO;
import com.automobileproject.EAP.dto.AssignEmployeeDTO;
import com.automobileproject.EAP.dto.ModificationRequestDTO;
import com.automobileproject.EAP.dto.QuoteRequestDTO;
import com.automobileproject.EAP.dto.UpdateNotesDTO;
import com.automobileproject.EAP.dto.UpdateStatusDTO;
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
import org.springframework.transaction.annotation.Transactional;

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
            Appointment.AppointmentStatus.AWAITING_PARTS);

    @Transactional
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

    @Transactional
    public Appointment createModificationRequest(ModificationRequestDTO dto, String customerEmail) {

        // 1. Find the logged-in customer
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + customerEmail));

        // 2. Find the vehicle from the request
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));

        // 3. --- CRITICAL SECURITY CHECK ---
        // Ensure the vehicle belongs to the logged-in customer
        // Access owner within transaction to avoid lazy loading issues
        if (!vehicle.getOwner().getId().equals(customer.getId())) {
            throw new AccessDeniedException("You do not have permission to book an appointment for this vehicle.");
        }

        // 4. --- NO VEHICLE BUSY CHECK FOR MODIFICATION REQUESTS ---
        // Modification requests are just quote requests - they don't require the
        // vehicle
        // to be physically available. A customer can request a quote for modifications
        // at any time, even if their vehicle is currently in the garage for another
        // service.

        // 5. Build the new modification request appointment
        Appointment appointment = Appointment.builder()
                .vehicle(vehicle)
                .service(null) // Modifications don't use standard services
                .appointmentDateTime(dto.getAppointmentDateTime()) // Optional
                .customerNotes(dto.getCustomerNotes())
                .appointmentType(Appointment.AppointmentType.MODIFICATION_PROJECT)
                // Status will be set to QUOTE_REQUESTED by @PrePersist
                .build();

        // 6. Save and return
        return appointmentRepository.save(appointment);
    }

    /**
     * Get all appointments filtered by status.
     * Only EMPLOYEE or ADMIN can access this.
     */
    public List<Appointment> getAppointmentsByStatus(Appointment.AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }

    /**
     * Get all appointments.
     * Only EMPLOYEE or ADMIN can access this.
     */
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    /**
     * Get appointments for a specific customer by email.
     * Only the CUSTOMER themselves can access this.
     */
    public List<Appointment> getAppointmentsByCustomerEmail(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + customerEmail));

        return appointmentRepository.findByVehicle_Owner(customer);
    }

    /**
     * Update the status of an appointment.
     * Only EMPLOYEE or ADMIN can update status.
     */
    @Transactional
    public Appointment updateAppointmentStatus(Long id, Appointment.AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        // Validate status transition (basic validation - can be enhanced)
        Appointment.AppointmentStatus currentStatus = appointment.getStatus();

        // Basic validation: can't update if already completed or cancelled
        if (currentStatus == Appointment.AppointmentStatus.COMPLETED ||
                currentStatus == Appointment.AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update status of a completed or cancelled appointment.");
        }

        appointment.setStatus(newStatus);
        return appointmentRepository.save(appointment);
    }

    /**
     * Update technician notes for an appointment.
     * Only EMPLOYEE or ADMIN can update notes.
     */
    @Transactional
    public Appointment updateTechnicianNotes(Long id, String notes) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        appointment.setTechnicianNotes(notes);
        return appointmentRepository.save(appointment);
    }

    /**
     * Submit a quote for a modification project.
     * Sets quotePrice, quoteDetails, and status to AWAITING_CUSTOMER_APPROVAL.
     * Only EMPLOYEE or ADMIN can submit quotes.
     */
    @Transactional
    public Appointment submitQuote(Long id, Double quotePrice, String quoteDetails) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        // Validate that this is a modification project
        if (appointment.getAppointmentType() != Appointment.AppointmentType.MODIFICATION_PROJECT) {
            throw new IllegalStateException("Quotes can only be submitted for modification projects.");
        }

        // Validate that status is QUOTE_REQUESTED
        if (appointment.getStatus() != Appointment.AppointmentStatus.QUOTE_REQUESTED) {
            throw new IllegalStateException(
                    "Quote can only be submitted for appointments with QUOTE_REQUESTED status.");
        }

        appointment.setQuotePrice(quotePrice);
        appointment.setQuoteDetails(quoteDetails);
        appointment.setStatus(Appointment.AppointmentStatus.AWAITING_CUSTOMER_APPROVAL);

        return appointmentRepository.save(appointment);
    }

    /**
     * Assign an employee to an appointment.
     * Only EMPLOYEE or ADMIN can assign employees.
     */
    @Transactional
    public Appointment assignEmployee(Long appointmentId, Long employeeId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        // Validate that the user is an employee
        if (employee.getRole() != User.Role.EMPLOYEE && employee.getRole() != User.Role.ADMIN) {
            throw new IllegalArgumentException("Only employees or admins can be assigned to appointments.");
        }

        // Add employee to assigned employees set (Set automatically handles duplicates)
        appointment.getAssignedEmployees().add(employee);

        return appointmentRepository.save(appointment);
    }
}
