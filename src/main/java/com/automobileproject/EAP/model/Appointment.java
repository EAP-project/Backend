package com.automobileproject.EAP.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Optional for modification requests, required for standard services
    // Note: @Future validation is handled at DTO level, not entity level
    private OffsetDateTime appointmentDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentType appointmentType;

    @Column(columnDefinition = "TEXT")
    private String customerNotes;

    @Column(columnDefinition = "TEXT")
    private String technicianNotes;

    private OffsetDateTime createdAt;

    // Quote fields (used for modification projects)
    private Double quotePrice;
    private String quoteDetails;
    private Boolean quoteApproved;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    // Optional for modification requests
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", nullable = true)
    private Service service;

    // Multiple services support
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "appointment_services", joinColumns = @JoinColumn(name = "appointment_id"), inverseJoinColumns = @JoinColumn(name = "service_id"))
    @Builder.Default
    private Set<Service> services = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "appointment_assignments", joinColumns = @JoinColumn(name = "appointment_id"), inverseJoinColumns = @JoinColumn(name = "employee_id"))
    @JsonIgnore
    @Builder.Default
    private Set<User> assignedEmployees = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();

        if (status == null) {
            if (appointmentType != null && appointmentType == AppointmentType.STANDARD_SERVICE) {
                status = AppointmentStatus.SCHEDULED;
            } else {
                status = AppointmentStatus.QUOTE_REQUESTED;
            }
        }
    }

    // --------------------------------------------
    // ENUMS CAN BE PLACED INSIDE THE SAME MODEL
    // --------------------------------------------

    public enum AppointmentStatus {
        // Standard statuses
        SCHEDULED,
        IN_PROGRESS, // Once the employee accepts the task
        AWAITING_PARTS,
        COMPLETED,
        CANCELLED,

        // Modification project statuses
        QUOTE_REQUESTED, // Customer has submitted a request
        AWAITING_CUSTOMER_APPROVAL, // Employee has submitted a quote
        REJECTED // Customer has rejected the quote
    }

    public enum AppointmentType {
        STANDARD_SERVICE,
        MODIFICATION_PROJECT
    }
}
