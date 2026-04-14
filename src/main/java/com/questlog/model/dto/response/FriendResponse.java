package com.questlog.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record FriendResponse(
        UUID friendshipId,
        UUID userId,
        String username,
        String avatarUrl,
        String status,
        String direction,
        LocalDateTime createdAt
) {}