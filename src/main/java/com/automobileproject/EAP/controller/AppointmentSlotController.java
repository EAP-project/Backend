package com.automobileproject.EAP.controller;

import com.automobileproject.EAP.dto.AppointmentSlotDTO;
import com.automobileproject.EAP.model.AppointmentSlot;
import com.automobileproject.EAP.service.AppointmentSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for managing appointment slot templates and checking availability.
 * Provides endpoints for checking slot availability (for chatbot and frontend).
 */
@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
public class AppointmentSlotController {

    private final AppointmentSlotService slotService;

    /**
     * Get all available slots for a specific date.
     * PUBLIC endpoint - can be accessed by chatbot without authentication.
     */
    @GetMapping("/available")
    public ResponseEntity<List<AppointmentSlotDTO>> getAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<AppointmentSlot> slots = slotService.getAvailableSlotsForDate(date);
        List<AppointmentSlotDTO> slotDTOs = slots.stream()
                .map(AppointmentSlotDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(slotDTOs);
    }

    /**
     * Get available slots for a specific date and session period.
     * PUBLIC endpoint - can be accessed by chatbot.
     */
    @GetMapping("/available/by-period")
    public ResponseEntity<List<AppointmentSlotDTO>> getAvailableSlotsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam AppointmentSlot.SessionPeriod period
    ) {
        List<AppointmentSlot> slots = slotService.getAvailableSlotsByPeriod(date, period);
        List<AppointmentSlotDTO> slotDTOs = slots.stream()
                .map(AppointmentSlotDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(slotDTOs);
    }

    /**
     * Check if a specific slot is available on a specific date.
     * Returns a simple boolean response.
     */
    @GetMapping("/check-availability")
    public ResponseEntity<Map<String, Object>> checkSlotAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam AppointmentSlot.SessionPeriod period,
            @RequestParam Integer slotNumber
    ) {
        boolean available = slotService.isSlotAvailable(date, period, slotNumber);
        AppointmentSlot slot = slotService.findSlotTemplate(period, slotNumber);

        return ResponseEntity.ok(Map.of(
                "available", available,
                "date", date,
                "slot", AppointmentSlotDTO.fromEntity(slot)
        ));
    }

    /**
     * Get count of available slots for a date.
     * Useful for quick availability summary.
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getAvailableSlotCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AppointmentSlot.SessionPeriod period
    ) {
        Long count;
        if (period != null) {
            count = slotService.countAvailableSlotsByPeriod(date, period);
        } else {
            count = slotService.countAvailableSlots(date);
        }

        return ResponseEntity.ok(Map.of(
                "date", date,
                "period", period != null ? period.name() : "ALL",
                "availableSlots", count
        ));
    }

    /**
     * Get all slot templates (10 total).
     * For admin/employee dashboard.
     */
    @GetMapping("/templates")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<AppointmentSlotDTO>> getAllSlotTemplates() {
        List<AppointmentSlot> slots = slotService.getAllSlotTemplates();
        List<AppointmentSlotDTO> slotDTOs = slots.stream()
                .map(AppointmentSlotDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(slotDTOs);
    }
}

