package com.questlog.service;

import com.questlog.model.dto.response.ActivityFeedResponse;
import com.questlog.model.entity.ActivityFeed;
import com.questlog.model.entity.Friendship;
import com.questlog.model.entity.User;
import com.questlog.repository.ActivityFeedRepository;
import com.questlog.repository.FriendshipRepository;
import com.questlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityFeedRepository activityFeedRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ActivityFeedResponse toResponse(ActivityFeed a) {
        return new ActivityFeedResponse(
                a.getId(),
                a.getUser().getUsername(),
                a.getUser().getAvatarUrl(),
                a.getActivityType(),
                a.getRawgGameId(),
                a.getGameTitle(),
                a.getDetails(),
                a.getCreatedAt()
        );
    }

    // Get friends activity feed
    public List<ActivityFeedResponse> getFriendsFeed() {
        User user = getCurrentUser();

        // Get all accepted friends
        List<Friendship> friendships =
                friendshipRepository.findAcceptedFriendships(user);

        List<User> friendUsers = new ArrayList<>();
        friendUsers.add(user); // include own activity too

        for (Friendship f : friendships) {
            if (f.getRequester().getId().equals(user.getId())) {
                friendUsers.add(f.getReceiver());
            } else {
                friendUsers.add(f.getRequester());
            }
        }

        return activityFeedRepository
                .findByUsersOrderByCreatedAtDesc(friendUsers)
                .stream()
                .limit(50)
                .map(this::toResponse)
                .toList();
    }

    // Get activity for a specific user
    public List<ActivityFeedResponse> getUserActivity(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return activityFeedRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .limit(20)
                .map(this::toResponse)
                .toList();
    }
}