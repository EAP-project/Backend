package com.automobileproject.EAP.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeLogDTO {

    private Long id;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String notes;
    private Long appointmentId;
    private Long employeeId;
    private String employeeFirstName;
    private String employeeLastName;
    private String employeeEmail;
    private Long durationMinutes; // Calculated duration in minutes
    private String formattedDuration; // Duration in HH:MM:SS format

    // Additional fields for displaying appointment context
    private String serviceName;
    private String vehicleModel;
    private String vehicleNumber;
}
