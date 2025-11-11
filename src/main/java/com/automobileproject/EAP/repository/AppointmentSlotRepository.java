package com.automobileproject.EAP.repository;

import com.automobileproject.EAP.model.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {

    /**
     * Find all slots for a specific session period
     */
    List<AppointmentSlot> findBySessionPeriod(AppointmentSlot.SessionPeriod sessionPeriod);

    /**
     * Find a specific slot by session period and slot number
     */
    Optional<AppointmentSlot> findBySessionPeriodAndSlotNumber(
            AppointmentSlot.SessionPeriod sessionPeriod,
            Integer slotNumber
    );

    /**
     * Check if a specific slot template exists
     */
    boolean existsBySessionPeriodAndSlotNumber(
            AppointmentSlot.SessionPeriod sessionPeriod,
            Integer slotNumber
    );
}

