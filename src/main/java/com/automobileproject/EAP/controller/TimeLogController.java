package com.automobileproject.EAP.controller;

import com.automobileproject.EAP.dto.CreateTimeLogDTO;
import com.automobileproject.EAP.dto.TimeLogDTO;
import com.automobileproject.EAP.service.TimeLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-logs")
@RequiredArgsConstructor
public class TimeLogController {

    private final TimeLogService timeLogService;

    /**
     * Get all time logs for the logged-in employee.
     */
    @GetMapping("/my-logs")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<TimeLogDTO>> getMyTimeLogs(Authentication authentication) {
        String employeeEmail = authentication.getName();
        List<TimeLogDTO> timeLogs = timeLogService.getMyTimeLogs(employeeEmail);
        return ResponseEntity.ok(timeLogs);
    }

    /**
     * Get all time logs across all employees (for admin).
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TimeLogDTO>> getAllTimeLogs() {
        List<TimeLogDTO> timeLogs = timeLogService.getAllTimeLogs();
        return ResponseEntity.ok(timeLogs);
    }

    /**
     * Get all time logs for a specific appointment.
     */
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<TimeLogDTO>> getTimeLogsByAppointment(
            @PathVariable Long appointmentId) {
        List<TimeLogDTO> timeLogs = timeLogService.getTimeLogsByAppointmentId(appointmentId);
        return ResponseEntity.ok(timeLogs);
    }

    /**
     * Create a new time log for an appointment.
     */
    @PostMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<TimeLogDTO> createTimeLog(
            @PathVariable Long appointmentId,
            @Valid @RequestBody CreateTimeLogDTO dto,
            Authentication authentication) {
        String employeeEmail = authentication.getName();
        TimeLogDTO timeLog = timeLogService.createTimeLog(appointmentId, dto, employeeEmail);
        return ResponseEntity.ok(timeLog);
    }
}
