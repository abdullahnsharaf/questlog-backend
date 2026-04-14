package com.questlog.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateGameRequest(

        String status,

        @Min(1) @Max(10)
        Integer personalRating,

        String reviewText,

        Float hoursPlayed

) {}