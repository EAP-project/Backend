package com.automobileproject.EAP.service;

import com.automobileproject.EAP.dto.CreateTimeLogDTO;
import com.automobileproject.EAP.dto.TimeLogDTO;
import com.automobileproject.EAP.model.Appointment;
import com.automobileproject.EAP.model.TimeLog;
import com.automobileproject.EAP.model.User;
import com.automobileproject.EAP.repository.AppointmentRepository;
import com.automobileproject.EAP.repository.TimeLogRepository;
import com.automobileproject.EAP.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeLogService {

    private final TimeLogRepository timeLogRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    /**
     * Get all time logs for a specific appointment.
     * Only EMPLOYEE or ADMIN can access this.
     */
    @Transactional(readOnly = true)
    public List<TimeLogDTO> getTimeLogsByAppointmentId(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        // Use eager fetching query to avoid LazyInitializationException
        List<TimeLog> timeLogs = timeLogRepository.findByAppointmentIdWithRelations(appointmentId);

        return timeLogs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new time log for an appointment.
     * Only EMPLOYEE can create time logs.
     * Employee must be assigned to the appointment.
     */
    @Transactional
    public TimeLogDTO createTimeLog(Long appointmentId, CreateTimeLogDTO dto, String employeeEmail) {
        // 1. Find the appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        // 2. Find the employee
        User employee = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + employeeEmail));

        // 3. Verify employee is assigned to the appointment
        boolean isAssigned = appointment.getAssignedEmployees().stream()
                .anyMatch(user -> user.getId().equals(employee.getId()));

        if (!isAssigned) {
            throw new AccessDeniedException("You are not assigned to this appointment.");
        }

        // 4. Validate that endTime is after startTime if both are provided
        if (dto.getEndTime() != null && dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        // 5. Create and save the time log
        TimeLog timeLog = TimeLog.builder()
                .appointment(appointment)
                .employee(employee)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .notes(dto.getNotes())
                .build();

        TimeLog savedTimeLog = timeLogRepository.save(timeLog);

        return convertToDTO(savedTimeLog);
    }

    /**
     * Convert TimeLog entity to DTO.
     */
    private TimeLogDTO convertToDTO(TimeLog timeLog) {
        TimeLogDTO.TimeLogDTOBuilder builder = TimeLogDTO.builder()
                .id(timeLog.getId())
                .startTime(timeLog.getStartTime())
                .endTime(timeLog.getEndTime())
                .notes(timeLog.getNotes())
                .appointmentId(timeLog.getAppointment().getId())
                .employeeId(timeLog.getEmployee().getId())
                .employeeFirstName(timeLog.getEmployee().getFirstName())
                .employeeLastName(timeLog.getEmployee().getLastName())
                .employeeEmail(timeLog.getEmployee().getEmail());

        // Calculate duration if endTime is provided
        if (timeLog.getEndTime() != null && timeLog.getStartTime() != null) {
            Duration duration = Duration.between(timeLog.getStartTime(), timeLog.getEndTime());
            builder.durationMinutes(duration.toMinutes());
        }

        return builder.build();
    }
}

