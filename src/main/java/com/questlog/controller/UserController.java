package com.questlog.controller;

import com.questlog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @GetMapping("/{username}")
    public ResponseEntity<Map<String, Object>> getPublicProfile(
            @PathVariable String username) {
        return ResponseEntity.ok(userService.getPublicProfile(username));
    }
}