package com.questlog.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String senderUsername,
        String type,
        String message,
        Boolean isRead,
        LocalDateTime createdAt
) {}