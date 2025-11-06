package com.automobileproject.EAP.repository;

import com.automobileproject.EAP.model.Notification;
import com.automobileproject.EAP.model.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    long countByUser_IdAndStatus(Long userId, NotificationStatus status);
}
