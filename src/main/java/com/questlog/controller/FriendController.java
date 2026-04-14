package com.questlog.controller;

import com.questlog.model.dto.response.FriendResponse;
import com.questlog.model.entity.User;
import com.questlog.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    public ResponseEntity<List<FriendResponse>> getMyFriends() {
        return ResponseEntity.ok(friendService.getMyFriends());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FriendResponse>> getPending() {
        return ResponseEntity.ok(friendService.getPendingRequests());
    }

    @PostMapping("/request/{username}")
    public ResponseEntity<FriendResponse> sendRequest(
            @PathVariable String username) {
        return ResponseEntity.ok(friendService.sendRequest(username));
    }

    @PutMapping("/accept/{id}")
    public ResponseEntity<FriendResponse> acceptRequest(
            @PathVariable UUID id) {
        return ResponseEntity.ok(friendService.acceptRequest(id));
    }

    @DeleteMapping("/reject/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable UUID id) {
        friendService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, String>>> searchUsers(
            @RequestParam String q) {
        List<User> users = friendService.searchUsers(q);
        List<Map<String, String>> result = users.stream()
                .map(u -> Map.of(
                        "username", u.getUsername(),
                        "avatarUrl", u.getAvatarUrl() != null ? u.getAvatarUrl() : ""
                ))
                .toList();
        return ResponseEntity.ok(result);
    }
}