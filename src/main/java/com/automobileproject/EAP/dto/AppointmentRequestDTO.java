package com.automobileproject.EAP.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDTO {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    // Multiple services support
    @NotEmpty(message = "At least one service must be selected")
    private List<Long> serviceIds;

    @NotNull(message = "Appointment date and time are required")
    @Future(message = "Appointment must be in the future")
    private OffsetDateTime appointmentDateTime;

    private String customerNotes;
}