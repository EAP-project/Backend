package com.automobileproject.EAP.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignEmployeeDTO {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;
}

