package com.questlog.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddGameRequest(

        @NotNull(message = "Game ID is required")
        Integer rawgGameId,

        @NotBlank(message = "Game title is required")
        String gameTitle,

        String gameCoverUrl,

        @NotBlank(message = "Status is required")
        String status,

        @Min(1) @Max(10)
        Integer personalRating,

        String reviewText,

        Float hoursPlayed

) {}