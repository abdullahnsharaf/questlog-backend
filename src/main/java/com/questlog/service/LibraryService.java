package com.questlog.service;

import com.questlog.model.dto.request.AddGameRequest;
import com.questlog.model.dto.request.UpdateGameRequest;
import com.questlog.model.dto.response.UserGameResponse;
import com.questlog.model.entity.ActivityFeed;
import com.questlog.model.entity.User;
import com.questlog.model.entity.UserGame;
import com.questlog.repository.ActivityFeedRepository;
import com.questlog.repository.UserGameRepository;
import com.questlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final UserGameRepository userGameRepository;
    private final UserRepository userRepository;
    private final ActivityFeedRepository activityFeedRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserGameResponse toResponse(UserGame ug) {
        return new UserGameResponse(
                ug.getId(),
                ug.getRawgGameId(),
                ug.getGameTitle(),
                ug.getGameCoverUrl(),
                ug.getStatus(),
                ug.getPersonalRating(),
                ug.getReviewText(),
                ug.getHoursPlayed(),
                ug.getAddedAt(),
                ug.getUpdatedAt()
        );
    }

    private void logActivity(User user, String type, UserGame game,
                             Map<String, Object> details) {
        ActivityFeed activity = ActivityFeed.builder()
                .user(user)
                .activityType(type)
                .rawgGameId(game.getRawgGameId())
                .gameTitle(game.getGameTitle())
                .details(details)
                .build();
        activityFeedRepository.save(activity);
    }

    public List<UserGameResponse> getMyLibrary() {
        User user = getCurrentUser();
        return userGameRepository.findByUser(user)
                .stream().map(this::toResponse).toList();
    }

    public List<UserGameResponse> getLibraryByStatus(String status) {
        User user = getCurrentUser();
        return userGameRepository.findByUserAndStatus(user, status.toUpperCase())
                .stream().map(this::toResponse).toList();
    }

    public UserGameResponse addGame(AddGameRequest request) {
        User user = getCurrentUser();

        if (userGameRepository.existsByUserAndRawgGameId(user, request.rawgGameId())) {
            throw new IllegalArgumentException("Game already in your library");
        }

        UserGame userGame = UserGame.builder()
                .user(user)
                .rawgGameId(request.rawgGameId())
                .gameTitle(request.gameTitle())
                .gameCoverUrl(request.gameCoverUrl())
                .status(request.status().toUpperCase())
                .personalRating(request.personalRating())
                .reviewText(request.reviewText())
                .hoursPlayed(request.hoursPlayed())
                .build();

        UserGame saved = userGameRepository.save(userGame);

        // Log activity
        logActivity(user, "ADDED_GAME", saved,
                Map.of("status", saved.getStatus(),
                        "coverUrl", saved.getGameCoverUrl() != null
                                ? saved.getGameCoverUrl() : ""));

        return toResponse(saved);
    }

    public UserGameResponse updateGame(UUID id, UpdateGameRequest request) {
        User user = getCurrentUser();

        UserGame userGame = userGameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (!userGame.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized");
        }

        String oldStatus = userGame.getStatus();

        if (request.status() != null) userGame.setStatus(request.status().toUpperCase());
        if (request.personalRating() != null) userGame.setPersonalRating(request.personalRating());
        if (request.reviewText() != null) userGame.setReviewText(request.reviewText());
        if (request.hoursPlayed() != null) userGame.setHoursPlayed(request.hoursPlayed());

        UserGame saved = userGameRepository.save(userGame);

        // Log status change activity
        if (request.status() != null && !request.status().toUpperCase().equals(oldStatus)) {
            logActivity(user, "STATUS_CHANGED", saved,
                    Map.of("oldStatus", oldStatus,
                            "newStatus", saved.getStatus(),
                            "coverUrl", saved.getGameCoverUrl() != null
                                    ? saved.getGameCoverUrl() : ""));
        }

        return toResponse(saved);
    }

    public void deleteGame(UUID id) {
        User user = getCurrentUser();

        UserGame userGame = userGameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (!userGame.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized");
        }

        userGameRepository.delete(userGame);
    }
}