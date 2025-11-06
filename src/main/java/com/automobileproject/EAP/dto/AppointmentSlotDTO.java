package com.automobileproject.EAP.dto;

import com.automobileproject.EAP.model.AppointmentSlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO for returning appointment slot template information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentSlotDTO {

    private Long id;
    private AppointmentSlot.SessionPeriod sessionPeriod;
    private Integer slotNumber;
    private LocalTime startTime;
    private LocalTime endTime;
    private String slotDescription;
    private Boolean isAvailable; // For availability queries

    /**
     * Convert entity to DTO
     */
    public static AppointmentSlotDTO fromEntity(AppointmentSlot slot) {
        return AppointmentSlotDTO.builder()
                .id(slot.getId())
                .sessionPeriod(slot.getSessionPeriod())
                .slotNumber(slot.getSlotNumber())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .slotDescription(slot.getSlotDescription())
                .build();
    }
}

