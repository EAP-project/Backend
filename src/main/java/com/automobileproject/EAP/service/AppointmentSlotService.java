package com.automobileproject.EAP.service;

import com.automobileproject.EAP.model.AppointmentSlot;
import com.automobileproject.EAP.repository.AppointmentRepository;
import com.automobileproject.EAP.repository.AppointmentSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing appointment slot templates.
 *
 * AppointmentSlot table contains only 10 TEMPLATE rows (5 morning + 5 afternoon).
 * Actual booking dates are stored in the Appointment table.
 *
 * To check availability: Query Appointment table for that date + slot combination.
 */
@Service
@RequiredArgsConstructor
public class AppointmentSlotService {

    private final AppointmentSlotRepository slotRepository;
    private final AppointmentRepository appointmentRepository;

    /**
     * Get all slot templates (10 total)
     */
    public List<AppointmentSlot> getAllSlotTemplates() {
        return slotRepository.findAll();
    }

    /**
     * Get slot templates for a specific session period
     */
    public List<AppointmentSlot> getSlotTemplatesByPeriod(AppointmentSlot.SessionPeriod period) {
        return slotRepository.findBySessionPeriod(period);
    }

    /**
     * Get available slots for a specific date.
     * Returns slot templates that are NOT booked on that date.
     */
    public List<AppointmentSlot> getAvailableSlotsForDate(LocalDate date) {
        List<AppointmentSlot> allSlots = slotRepository.findAll();

        return allSlots.stream()
                .filter(slot -> !appointmentRepository.isSlotBookedOnDate(slot.getId(), date))
                .collect(Collectors.toList());
    }

    /**
     * Get available slots for a specific date and session period
     */
    public List<AppointmentSlot> getAvailableSlotsByPeriod(LocalDate date, AppointmentSlot.SessionPeriod period) {
        List<AppointmentSlot> periodSlots = slotRepository.findBySessionPeriod(period);

        return periodSlots.stream()
                .filter(slot -> !appointmentRepository.isSlotBookedOnDate(slot.getId(), date))
                .collect(Collectors.toList());
    }

    /**
     * Find a specific slot template by session period and slot number
     */
    public AppointmentSlot findSlotTemplate(AppointmentSlot.SessionPeriod period, Integer slotNumber) {
        // Validate slot number
        if (slotNumber < 1 || slotNumber > 5) {
            throw new IllegalArgumentException("Slot number must be between 1 and 5");
        }

        return slotRepository.findBySessionPeriodAndSlotNumber(period, slotNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Slot template not found: %s Slot %d", period, slotNumber)
                ));
    }

    /**
     * Check if a specific slot is available on a specific date
     */
    public boolean isSlotAvailable(LocalDate date, AppointmentSlot.SessionPeriod period, Integer slotNumber) {
        AppointmentSlot slot = findSlotTemplate(period, slotNumber);
        return !appointmentRepository.isSlotBookedOnDate(slot.getId(), date);
    }

    /**
     * Count available slots for a date
     */
    public Long countAvailableSlots(LocalDate date) {
        List<AppointmentSlot> allSlots = slotRepository.findAll();

        return allSlots.stream()
                .filter(slot -> !appointmentRepository.isSlotBookedOnDate(slot.getId(), date))
                .count();
    }

    /**
     * Count available slots for a date and period
     */
    public Long countAvailableSlotsByPeriod(LocalDate date, AppointmentSlot.SessionPeriod period) {
        List<AppointmentSlot> periodSlots = slotRepository.findBySessionPeriod(period);

        return periodSlots.stream()
                .filter(slot -> !appointmentRepository.isSlotBookedOnDate(slot.getId(), date))
                .count();
    }
}

