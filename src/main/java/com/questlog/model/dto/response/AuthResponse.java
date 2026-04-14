package com.questlog.model.dto.response;

import java.util.UUID;

public record AuthResponse(
        String token,
        UUID id,
        String username,
        String email,
        String avatarUrl
) {}