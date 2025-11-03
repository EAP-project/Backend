package com.automobileproject.EAP.controller;

import com.automobileproject.EAP.dto.AppointmentRequestDTO;
import com.automobileproject.EAP.model.Appointment;
import com.automobileproject.EAP.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

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
}