package com.automobileproject.EAP.controller;

import com.automobileproject.EAP.dto.NotificationDTO;
import com.automobileproject.EAP.model.Notification;
import com.automobileproject.EAP.repository.UserRepository;
import com.automobileproject.EAP.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public Page<NotificationDTO> list(Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var user = userRepository.findByEmail(auth.getName()).orElseThrow();
        Page<Notification> notifications = notificationService.getForUser(user.getId(), PageRequest.of(page, size));

        return notifications.map(n -> NotificationDTO.builder()
                .id(n.getId())
                .type(n.getType() != null ? n.getType().name() : null)
                .title(n.getTitle())
                .message(n.getMessage())
                .status(n.getStatus() != null ? n.getStatus().name() : null)
                .createdAt(n.getCreatedAt())
                .readAt(n.getReadAt())
                .userId(user.getId())
                .build());
    }

    @GetMapping("/unread-count")
    public long unreadCount(Authentication auth) {
        var user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return notificationService.countUnread(user.getId());
    }

    @PatchMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        notificationService.markRead(id);
    }

    @PatchMapping("/read-all")
    public void markAllRead(Authentication auth) {
        var user = userRepository.findByEmail(auth.getName()).orElseThrow();
        notificationService.markAllRead(user.getId());
    }
}
