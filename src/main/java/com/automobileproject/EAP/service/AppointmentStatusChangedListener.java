package com.automobileproject.EAP.service;

import com.automobileproject.EAP.event.AppointmentStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentStatusChangedListener {

    private final NotificationService notificationService;

    @EventListener
    public void onAppointmentStatusChanged(AppointmentStatusChangedEvent event) {
        notificationService.createAppointmentStatusChangeNotification(
                event.getUserId(),
                event.getAppointmentId(),
                event.getOldStatus(),
                event.getNewStatus());
    }
}
