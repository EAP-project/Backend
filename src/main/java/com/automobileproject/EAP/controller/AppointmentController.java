package com.automobileproject.EAP.controller;

import com.automobileproject.EAP.dto.AppointmentRequestDTO;
import com.automobileproject.EAP.dto.AssignEmployeeDTO;
import com.automobileproject.EAP.dto.CreateTimeLogDTO;
import com.automobileproject.EAP.dto.ModificationRequestDTO;
import com.automobileproject.EAP.dto.QuoteRequestDTO;
import com.automobileproject.EAP.dto.SlotBasedAppointmentRequestDTO;
import com.automobileproject.EAP.dto.TimeLogDTO;
import com.automobileproject.EAP.dto.UpdateNotesDTO;
import com.automobileproject.EAP.dto.UpdateStatusDTO;
import com.automobileproject.EAP.model.Appointment;
import com.automobileproject.EAP.service.AppointmentService;
import com.automobileproject.EAP.service.TimeLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final TimeLogService timeLogService;

    @PostMapping("/standard-service")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Appointment> createStandardAppointment(
            @Valid @RequestBody AppointmentRequestDTO request,
            Authentication authentication
    ) {
        String customerEmail = authentication.getName();
        Appointment newAppointment = appointmentService.createStandardAppointment(request, customerEmail);
        return new ResponseEntity<>(newAppointment, HttpStatus.CREATED);
    }

    /**
     * NEW: Create a standard appointment with slot-based booking.
     * This is the recommended endpoint for booking appointments with the new slot system.
     */
    @PostMapping("/slot-based-service")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Appointment> createSlotBasedAppointment(
            @Valid @RequestBody SlotBasedAppointmentRequestDTO request,
            Authentication authentication
    ) {
        String customerEmail = authentication.getName();
        Appointment newAppointment = appointmentService.createSlotBasedAppointment(request, customerEmail);
        return new ResponseEntity<>(newAppointment, HttpStatus.CREATED);
    }

    @PostMapping("/modification-request")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Appointment> createModificationRequest(
            @Valid @RequestBody ModificationRequestDTO request,
            Authentication authentication
    ) {
        String customerEmail = authentication.getName();
        Appointment newAppointment = appointmentService.createModificationRequest(request, customerEmail);
        return new ResponseEntity<>(newAppointment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/time-logs")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<TimeLogDTO>> getTimeLogs(
            @PathVariable Long id
    ) {
        List<TimeLogDTO> timeLogs = timeLogService.getTimeLogsByAppointmentId(id);
        return ResponseEntity.ok(timeLogs);
    }

    @PostMapping("/{id}/time-logs")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<TimeLogDTO> createTimeLog(
            @PathVariable Long id,
            @Valid @RequestBody CreateTimeLogDTO request,
            Authentication authentication
    ) {
        String employeeEmail = authentication.getName();
        TimeLogDTO newTimeLog = timeLogService.createTimeLog(id, request, employeeEmail);
        return new ResponseEntity<>(newTimeLog, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Appointment>> getAppointments(
            @RequestParam(required = false) Appointment.AppointmentStatus status
    ) {
        List<Appointment> appointments;
        if (status != null) {
            appointments = appointmentService.getAppointmentsByStatus(status);
        } else {
            // If no status filter, return all appointments
            appointments = appointmentService.getAllAppointments();
        }
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Appointment> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusDTO request
    ) {
        Appointment updatedAppointment = appointmentService.updateAppointmentStatus(id, request.getStatus());
        return ResponseEntity.ok(updatedAppointment);
    }

    @PutMapping("/{id}/notes")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Appointment> updateNotes(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNotesDTO request
    ) {
        Appointment updatedAppointment = appointmentService.updateTechnicianNotes(id, request.getTechnicianNotes());
        return ResponseEntity.ok(updatedAppointment);
    }

    @PostMapping("/{id}/quote")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Appointment> submitQuote(
            @PathVariable Long id,
            @Valid @RequestBody QuoteRequestDTO request
    ) {
        Appointment updatedAppointment = appointmentService.submitQuote(
                id,
                request.getQuotePrice(),
                request.getQuoteDetails()
        );
        return ResponseEntity.ok(updatedAppointment);
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Appointment> assignEmployee(
            @PathVariable Long id,
            @Valid @RequestBody AssignEmployeeDTO request
    ) {
        Appointment updatedAppointment = appointmentService.assignEmployee(id, request.getEmployeeId());
        return ResponseEntity.ok(updatedAppointment);
    }
}