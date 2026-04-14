package com.questlog.controller;

import com.questlog.model.dto.response.ActivityFeedResponse;
import com.questlog.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping("/feed")
    public ResponseEntity<List<ActivityFeedResponse>> getFeed() {
        return ResponseEntity.ok(activityService.getFriendsFeed());
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<ActivityFeedResponse>> getUserActivity(
            @PathVariable String username) {
        return ResponseEntity.ok(activityService.getUserActivity(username));
    }
}