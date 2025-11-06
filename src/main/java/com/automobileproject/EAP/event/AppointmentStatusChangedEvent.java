package com.automobileproject.EAP.event;

public class AppointmentStatusChangedEvent {
    private final Long appointmentId;
    private final Long userId;
    private final String oldStatus;
    private final String newStatus;

    public AppointmentStatusChangedEvent(Long appointmentId, Long userId, String oldStatus, String newStatus) {
        this.appointmentId = appointmentId;
        this.userId = userId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }
}
