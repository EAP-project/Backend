package com.automobileproject.EAP.event;

import com.automobileproject.EAP.controller.AppointmentController;
import org.springframework.context.ApplicationEvent;

public class AppointmentCreatedEvent extends ApplicationEvent {
    private final Long appointmentId;
    private final String customerName;

    public AppointmentCreatedEvent(Object source, Long appointmentId, String customerName, String time) {
        super(source);
        this.appointmentId = appointmentId;
        this.customerName = customerName;
    }

    public AppointmentCreatedEvent(AppointmentController source, Long appointmentId, String customerName) {
        super(source);
        this.appointmentId = appointmentId;
        this.customerName = customerName;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public String getCustomerName() {
        return customerName;
    }



}
