package com.questlog.service;

import com.questlog.model.dto.response.NotificationResponse;
import com.questlog.model.entity.Notification;
import com.questlog.model.entity.User;
import com.questlog.repository.NotificationRepository;
import com.questlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getSender() != null ? n.getSender().getUsername() : "System",
                n.getType(),
                n.getMessage(),
                n.getIsRead(),
                n.getCreatedAt()
        );
    }

    // Called by other services to create notifications
    public void createNotification(User recipient, User sender,
                                   String type, String message) {
        if (recipient.getId().equals(sender.getId())) return;

        Notification notification = Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(type)
                .message(message)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getMyNotifications() {
        User user = getCurrentUser();
        return notificationRepository
                .findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Map<String, Long> getUnreadCount() {
        User user = getCurrentUser();
        long count = notificationRepository
                .countByRecipientAndIsRead(user, false);
        return Map.of("count", count);
    }

    public NotificationResponse markAsRead(UUID id) {
        User user = getCurrentUser();

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized");
        }

        notification.setIsRead(true);
        return toResponse(notificationRepository.save(notification));
    }

    public void markAllAsRead() {
        User user = getCurrentUser();
        List<Notification> unread = notificationRepository
                .findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .filter(n -> !n.getIsRead())
                .toList();

        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }
}