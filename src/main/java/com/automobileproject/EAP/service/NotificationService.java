package com.automobileproject.EAP.service;

import com.automobileproject.EAP.model.Notification;
import com.automobileproject.EAP.model.User;
import com.automobileproject.EAP.model.enums.NotificationStatus;
import com.automobileproject.EAP.model.enums.NotificationType;
import com.automobileproject.EAP.repository.NotificationRepository;
import com.automobileproject.EAP.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createAppointmentStatusChangeNotification(@NonNull Long userId, Long appointmentId, String oldStatus,
            String newStatus) {
        User user = userRepository.findById(userId).orElseThrow();
        Notification n = Notification.builder()
                .user(user)
                .type(NotificationType.APPOINTMENT_STATUS_CHANGED)
                .title("Appointment status updated")
                .message("Appointment #" + appointmentId + " changed from " + oldStatus + " to " + newStatus + ".")
                .build();
        notificationRepository.save(Objects.requireNonNull(n));
    }

    @Transactional(readOnly = true)
    public Page<Notification> getForUser(@NonNull Long userId, Pageable pageable) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public long countUnread(@NonNull Long userId) {
        return notificationRepository.countByUser_IdAndStatus(userId, NotificationStatus.UNREAD);
    }

    @Transactional
    public void markRead(@NonNull Long notificationId) {
        Notification n = notificationRepository.findById(notificationId).orElseThrow();
        if (n.getStatus() == NotificationStatus.UNREAD) {
            n.setStatus(NotificationStatus.READ);
            n.setReadAt(LocalDateTime.now());
        }
    }

    @Transactional
    public void markAllRead(@NonNull Long userId) {
        // Simple implementation: page through and mark read
        notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId, Pageable.unpaged())
                .forEach(n -> {
                    if (n.getStatus() == NotificationStatus.UNREAD) {
                        n.setStatus(NotificationStatus.READ);
                        n.setReadAt(LocalDateTime.now());
                    }
                });
    }
}
