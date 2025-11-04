package com.automobileproject.EAP.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteRequestDTO {

    @NotNull(message = "Quote price is required")
    @Positive(message = "Quote price must be positive")
    private Double quotePrice;

    @NotBlank(message = "Quote details are required")
    private String quoteDetails;
}

