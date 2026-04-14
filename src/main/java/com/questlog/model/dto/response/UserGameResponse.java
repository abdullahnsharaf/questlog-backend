package com.questlog.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserGameResponse(
        UUID id,
        Integer rawgGameId,
        String gameTitle,
        String gameCoverUrl,
        String status,
        Integer personalRating,
        String reviewText,
        Float hoursPlayed,
        LocalDateTime addedAt,
        LocalDateTime updatedAt
) {}