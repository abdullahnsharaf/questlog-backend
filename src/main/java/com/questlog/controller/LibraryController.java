package com.questlog.controller;

import com.questlog.model.dto.request.AddGameRequest;
import com.questlog.model.dto.request.UpdateGameRequest;
import com.questlog.model.dto.response.UserGameResponse;
import com.questlog.service.LibraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    @GetMapping
    public ResponseEntity<List<UserGameResponse>> getMyLibrary() {
        return ResponseEntity.ok(libraryService.getMyLibrary());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<UserGameResponse>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(libraryService.getLibraryByStatus(status));
    }

    @PostMapping
    public ResponseEntity<UserGameResponse> addGame(
            @Valid @RequestBody AddGameRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(libraryService.addGame(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserGameResponse> updateGame(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGameRequest request) {
        return ResponseEntity.ok(libraryService.updateGame(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable UUID id) {
        libraryService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}