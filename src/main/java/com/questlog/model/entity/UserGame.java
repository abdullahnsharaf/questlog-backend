package com.questlog.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_games")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGame {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rawg_game_id", nullable = false)
    private Integer rawgGameId;

    @Column(name = "game_title", nullable = false)
    private String gameTitle;

    @Column(name = "game_cover_url")
    private String gameCoverUrl;

    @Column(nullable = false)
    private String status;

    @Column(name = "personal_rating")
    private Integer personalRating;

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    @Column(name = "hours_played")
    private Float hoursPlayed;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}