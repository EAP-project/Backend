package com.automobileproject.EAP.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotesDTO {

    @NotBlank(message = "Technician notes are required")
    private String technicianNotes;
}

