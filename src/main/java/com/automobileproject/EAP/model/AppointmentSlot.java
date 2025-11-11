package com.automobileproject.EAP.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Represents a slot template for appointments.
 * The garage has 10 slot templates: 5 MORNING and 5 AFTERNOON.
 *
 * This is a TEMPLATE table - only 10 rows total:
 * - MORNING slots 1-5 (7:00-12:00)
 * - AFTERNOON slots 1-5 (13:00-18:00)
 *
 * The actual DATE is stored in the Appointment table's appointmentDateTime field.
 * To check if a slot is available for a specific date:
 * - Query Appointment table for appointments on that date with this slot
 */
@Entity
@Table(name = "appointment_slots",
       uniqueConstraints = @UniqueConstraint(columnNames = {"session_period", "slot_number"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "session_period")
    private SessionPeriod sessionPeriod;

    @Column(nullable = false, name = "slot_number")
    private Integer slotNumber; // 1-5

    @Column(nullable = false, name = "start_time")
    private LocalTime startTime;

    @Column(nullable = false, name = "end_time")
    private LocalTime endTime;


    public enum SessionPeriod {
        MORNING,    // 7:00 AM - 12:00 PM
        AFTERNOON   // 1:00 PM - 6:00 PM
    }

    /**
     * Get a human-readable description of this slot
     */
    public String getSlotDescription() {
        return String.format("%s Slot %d (%s - %s)",
                sessionPeriod.name(),
                slotNumber,
                startTime.toString(),
                endTime.toString());
    }

    /**
     * Helper method to get the default time range based on session and slot number
     */
    public static LocalTime getDefaultStartTime(SessionPeriod period, Integer slotNumber) {
        if (period == SessionPeriod.MORNING) {
            // Morning slots: 7:00, 8:00, 9:00, 10:00, 11:00
            return LocalTime.of(6 + slotNumber, 0);
        } else {
            // Afternoon slots: 13:00, 14:00, 15:00, 16:00, 17:00
            return LocalTime.of(12 + slotNumber, 0);
        }
    }

    /**
     * Helper method to get the default end time (1 hour after start)
     */
    public static LocalTime getDefaultEndTime(SessionPeriod period, Integer slotNumber) {
        return getDefaultStartTime(period, slotNumber).plusHours(1);
    }
}

