package com.automobileproject.EAP.dto;

import com.automobileproject.EAP.model.AppointmentSlot;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for creating appointments with slot-based booking
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotBasedAppointmentRequestDTO {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

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

