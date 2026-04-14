package com.questlog.service;

import com.questlog.model.dto.response.UserGameResponse;
import com.questlog.model.entity.User;
import com.questlog.repository.UserGameRepository;
import com.questlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserGameRepository userGameRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Map<String, Object> getMyProfile() {
        User user = getCurrentUser();
        return buildProfile(user);
    }

    public Map<String, Object> getPublicProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return buildProfile(user);
    }

    private Map<String, Object> buildProfile(User user) {
        List<UserGameResponse> games = userGameRepository.findByUser(user)
                .stream()
                .map(ug -> new UserGameResponse(
                        ug.getId(), ug.getRawgGameId(), ug.getGameTitle(),
                        ug.getGameCoverUrl(), ug.getStatus(),
                        ug.getPersonalRating(), ug.getReviewText(),
                        ug.getHoursPlayed(), ug.getAddedAt(), ug.getUpdatedAt()))
                .toList();

        long playing   = games.stream().filter(g -> "PLAYING".equals(g.status())).count();
        long completed = games.stream().filter(g -> "COMPLETED".equals(g.status())).count();
        long wishlist  = games.stream().filter(g -> "WISHLIST".equals(g.status())).count();

        return Map.of(
                "id",           user.getId(),
                "username",     user.getUsername(),
                "email",        user.getEmail(),
                "avatarUrl",    user.getAvatarUrl() != null ? user.getAvatarUrl() : "",
                "bio",          user.getBio() != null ? user.getBio() : "",
                "createdAt",    user.getCreatedAt(),
                "stats", Map.of(
                        "totalGames", games.size(),
                        "playing",    playing,
                        "completed",  completed,
                        "wishlist",   wishlist
                ),
                "recentGames",  games.stream().limit(6).toList()
        );
    }
}