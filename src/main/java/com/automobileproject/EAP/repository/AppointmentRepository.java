package com.automobileproject.EAP.repository;

import com.automobileproject.EAP.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Checks if a vehicle has any appointments that mean it is
     * physically in the garage (e.g., IN_PROGRESS or AWAITING_PARTS).
     */
    boolean existsByVehicleIdAndStatusIn(Long vehicleId, List<Appointment.AppointmentStatus> statuses);

    /**
     * Find all appointments with a specific status.
     */
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);

    /**
     * Check if a specific slot is booked on a specific date.
     * Checks if there's an appointment with the given slot on the given date.
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM Appointment a " +
           "WHERE a.appointmentSlot.id = :slotId " +
           "AND CAST(a.appointmentDateTime AS date) = :date " +
           "AND a.status != 'CANCELLED'")
    boolean isSlotBookedOnDate(@Param("slotId") Long slotId, @Param("date") LocalDate date);

    /**
     * Find all appointments for a specific date and slot
     */
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.appointmentSlot.id = :slotId " +
           "AND CAST(a.appointmentDateTime AS date) = :date " +
           "AND a.status != 'CANCELLED'")
    List<Appointment> findBySlotIdAndDate(@Param("slotId") Long slotId, @Param("date") LocalDate date);

    /**
     * Find all appointments on a specific date
     */
    @Query("SELECT a FROM Appointment a " +
           "WHERE CAST(a.appointmentDateTime AS date) = :date " +
           "AND a.status != 'CANCELLED'")
    List<Appointment> findByDate(@Param("date") LocalDate date);
}
