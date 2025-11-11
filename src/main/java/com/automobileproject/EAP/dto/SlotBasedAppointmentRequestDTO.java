package com.automobileproject.EAP.dto;

import com.automobileproject.EAP.model.AppointmentSlot;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for creating appointments with slot-based booking
 * Supports multiple services per appointment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotBasedAppointmentRequestDTO {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    // Kept for backward compatibility (optional)
    private Long serviceId;

    // NEW: Support multiple services
    @NotEmpty(message = "At least one service must be selected")
    private List<Long> serviceIds;

    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDate appointmentDate;

    @NotNull(message = "Session period is required")
    private AppointmentSlot.SessionPeriod sessionPeriod;

    @NotNull(message = "Slot number is required")
    @Min(value = 1, message = "Slot number must be between 1 and 5")
    @Max(value = 5, message = "Slot number must be between 1 and 5")
    private Integer slotNumber;

    private String customerNotes;
}

