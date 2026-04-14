package com.questlog.service;

import com.questlog.model.dto.response.FriendResponse;
import com.questlog.model.entity.Friendship;
import com.questlog.model.entity.User;
import com.questlog.repository.FriendshipRepository;
import com.questlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private FriendResponse toResponse(Friendship f, User currentUser) {
        boolean isRequester = f.getRequester().getId().equals(currentUser.getId());
        User otherUser = isRequester ? f.getReceiver() : f.getRequester();
        return new FriendResponse(
                f.getId(),
                otherUser.getId(),
                otherUser.getUsername(),
                otherUser.getAvatarUrl(),
                f.getStatus(),
                isRequester ? "SENT" : "RECEIVED",
                f.getCreatedAt()
        );
    }

    public List<FriendResponse> getMyFriends() {
        User user = getCurrentUser();
        return friendshipRepository.findAcceptedFriendships(user)
                .stream().map(f -> toResponse(f, user)).toList();
    }

    public List<FriendResponse> getPendingRequests() {
        User user = getCurrentUser();
        return friendshipRepository.findByReceiverAndStatus(user, "PENDING")
                .stream().map(f -> toResponse(f, user)).toList();
    }

    public FriendResponse sendRequest(String username) {
        User currentUser = getCurrentUser();

        User targetUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (targetUser.getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Cannot add yourself");
        }

        friendshipRepository.findBetweenUsers(currentUser, targetUser)
                .ifPresent(f -> {
                    throw new IllegalArgumentException("Friend request already exists");
                });

        Friendship friendship = Friendship.builder()
                .requester(currentUser)
                .receiver(targetUser)
                .status("PENDING")
                .build();

        Friendship saved = friendshipRepository.save(friendship);

        // Notify the receiver
        notificationService.createNotification(
                targetUser, currentUser,
                "FRIEND_REQUEST",
                currentUser.getUsername() + " sent you a friend request"
        );

        return toResponse(saved, currentUser);
    }

    public FriendResponse acceptRequest(UUID friendshipId) {
        User currentUser = getCurrentUser();

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!friendship.getReceiver().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized");
        }

        friendship.setStatus("ACCEPTED");
        Friendship saved = friendshipRepository.save(friendship);

        // Notify the requester
        notificationService.createNotification(
                friendship.getRequester(), currentUser,
                "FRIEND_ACCEPTED",
                currentUser.getUsername() + " accepted your friend request"
        );

        return toResponse(saved, currentUser);
    }

    public void deleteRequest(UUID friendshipId) {
        User currentUser = getCurrentUser();

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        boolean isInvolved =
                friendship.getRequester().getId().equals(currentUser.getId()) ||
                        friendship.getReceiver().getId().equals(currentUser.getId());

        if (!isInvolved) throw new RuntimeException("Not authorized");

        friendshipRepository.delete(friendship);
    }

    public List<User> searchUsers(String query) {
        User currentUser = getCurrentUser();
        return userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .filter(u -> u.getUsername().toLowerCase()
                        .contains(query.toLowerCase()))
                .limit(10)
                .toList();
    }
}