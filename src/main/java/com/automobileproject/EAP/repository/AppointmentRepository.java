package com.automobileproject.EAP.repository;

import com.automobileproject.EAP.model.Appointment;
import com.automobileproject.EAP.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
     * Find all appointments for a specific customer (vehicle owner).
     */
    List<Appointment> findByVehicle_Owner(User owner);

    /**
     * Find appointments assigned to a specific employee with a specific status.
     */
    List<Appointment> findByAssignedEmployeesContainingAndStatus(User employee, Appointment.AppointmentStatus status);
}
