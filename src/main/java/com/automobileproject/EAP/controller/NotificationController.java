package com.automobileproject.EAP.controller;

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
    public Page<Notification> list(Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return notificationService.getForUser(user.getId(), PageRequest.of(page, size));
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
