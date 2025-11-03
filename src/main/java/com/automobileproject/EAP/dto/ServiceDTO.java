package com.automobileproject.EAP.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {

    @NotBlank(message = "Service name is required")
    private String name;

    private String description;

    @Positive(message = "Estimated cost must be positive")
    private Double estimatedCost;

    @Positive(message = "Estimated duration must be positive")
    private Integer estimatedDurationMinutes;
}