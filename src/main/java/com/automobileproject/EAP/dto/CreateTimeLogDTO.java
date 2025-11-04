package com.automobileproject.EAP.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTimeLogDTO {

    @NotNull(message = "Start time is required")
    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    private String notes;
}

