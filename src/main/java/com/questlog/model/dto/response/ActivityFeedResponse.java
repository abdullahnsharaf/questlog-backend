package com.questlog.model.dto.response;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record ActivityFeedResponse(
        UUID id,
        String username,
        String avatarUrl,
        String activityType,
        Integer rawgGameId,
        String gameTitle,
        Map<String, Object> details,
        LocalDateTime createdAt
) {}