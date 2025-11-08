// java
package com.automobileproject.EAP.service;

import com.automobileproject.EAP.dto.NotificationDTO;
import com.automobileproject.EAP.model.Appointment;
import com.automobileproject.EAP.event.AppointmentCreatedEvent;
import org.springframework.context.event.EventListener;
import com.automobileproject.EAP.repository.AppointmentRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;



@Service
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private final AppointmentRepository appointmentRepository;

    public NotificationService(SimpMessagingTemplate messagingTemplate,AppointmentRepository appointmentRepository) {
        this.messagingTemplate = messagingTemplate;
        this.appointmentRepository = appointmentRepository;
    }

    // Employee notification: new appoinment created
    @Async
    @EventListener
    public void onAppointmentCreated(AppointmentCreatedEvent evt) {
        Appointment appointment = appointmentRepository.findById(evt.getAppointmentId())
                .orElse(null);

        String message;
        String appointmentDate = appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        NotificationDTO notification_employee = new NotificationDTO(
                "New appointment",
                "Appointment for " + evt.getCustomerName() + " on " + appointmentDate,
                String.valueOf(evt.getAppointmentId()),
                "NEW_APPOINTMENT",
                "EMPLOYEE"
        );
        messagingTemplate.convertAndSend("/topic/notifications/employee", notification_employee);
    }
}
