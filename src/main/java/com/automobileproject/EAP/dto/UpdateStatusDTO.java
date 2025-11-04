package com.automobileproject.EAP.dto;

import com.automobileproject.EAP.model.Appointment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusDTO {

    @NotNull(message = "Status is required")
    private Appointment.AppointmentStatus status;
}

