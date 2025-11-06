package com.automobileproject.EAP.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCategoryDTO {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;


}