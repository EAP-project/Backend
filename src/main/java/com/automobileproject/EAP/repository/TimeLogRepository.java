package com.automobileproject.EAP.repository;

import com.automobileproject.EAP.model.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeLogRepository extends JpaRepository<TimeLog, Long> {

    /**
     * Find all time logs for a specific appointment with eager loading of
     * relationships.
     */
    @Query("SELECT tl FROM TimeLog tl " +
            "LEFT JOIN FETCH tl.appointment " +
            "LEFT JOIN FETCH tl.employee " +
            "WHERE tl.appointment.id = :appointmentId")
    List<TimeLog> findByAppointmentIdWithRelations(@Param("appointmentId") Long appointmentId);

    /**
     * Find all time logs for a specific appointment.
     */
    List<TimeLog> findByAppointmentId(Long appointmentId);

    /**
     * Find active time logs (where endTime is null) for a specific appointment.
     */
    List<TimeLog> findByAppointmentIdAndEndTimeIsNull(Long appointmentId);

    /**
     * Find active time log for a specific employee and appointment.
     */
    Optional<TimeLog> findByAppointmentIdAndEmployeeIdAndEndTimeIsNull(Long appointmentId, Long employeeId);

    /**
     * Find all time logs for a specific employee with eager loading of
     * relationships.
     */
    @Query("SELECT tl FROM TimeLog tl " +
            "LEFT JOIN FETCH tl.appointment a " +
            "LEFT JOIN FETCH a.service " +
            "LEFT JOIN FETCH a.vehicle " +
            "LEFT JOIN FETCH tl.employee " +
            "WHERE tl.employee.id = :employeeId " +
            "ORDER BY tl.startTime DESC")
    List<TimeLog> findByEmployeeIdWithRelations(@Param("employeeId") Long employeeId);

    /**
     * Find all time logs across all employees with eager loading of relationships
     * (for admin).
     */
    @Query("SELECT tl FROM TimeLog tl " +
            "LEFT JOIN FETCH tl.appointment a " +
            "LEFT JOIN FETCH a.service " +
            "LEFT JOIN FETCH a.vehicle " +
            "LEFT JOIN FETCH tl.employee " +
            "ORDER BY tl.startTime DESC")
    List<TimeLog> findAllWithRelations();
}
