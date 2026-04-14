package com.questlog.controller;

import com.questlog.service.RawgService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final RawgService rawgService;

    @GetMapping("/search")
    public ResponseEntity<Map> searchGames(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(rawgService.searchGames(q, page));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<Map> getGame(@PathVariable int gameId) {
        return ResponseEntity.ok(rawgService.getGameById(gameId));
    }

    @GetMapping("/trending")
    public ResponseEntity<Map> getTrending() {
        return ResponseEntity.ok(rawgService.getTrendingGames());
    }

    @GetMapping("/new-releases")
    public ResponseEntity<Map> getNewReleases() {
        return ResponseEntity.ok(rawgService.getNewReleases());
    }
}