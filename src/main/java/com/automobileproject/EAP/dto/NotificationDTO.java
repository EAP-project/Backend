package com.automobileproject.EAP.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long id;
    private String type;
    private String title;
    private String message;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private Long userId;
}
