package com.automobileproject.EAP.dto;

public class NotificationDTO {
    private String title;
    private String message;
    private String appointmentId;

    private String notificationType;  // NEW
    private String targetRole;        // NEW: "EMPLOYEE", "CUSTOMER", "ADMIN"
    private Long timestamp;

    public NotificationDTO() {}

    public NotificationDTO(String title, String message, String appointmentId,
                           String notificationType, String targetRole) {
        this.title = title;
        this.message = message;
        this.appointmentId = appointmentId;
        this.notificationType = notificationType;
        this.targetRole = targetRole;
        this.timestamp = System.currentTimeMillis();
    }

    // getters / setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}


