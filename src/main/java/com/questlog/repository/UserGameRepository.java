package com.questlog.repository;

import com.questlog.model.entity.UserGame;
import com.questlog.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserGameRepository extends JpaRepository<UserGame, UUID> {

    List<UserGame> findByUser(User user);

    List<UserGame> findByUserAndStatus(User user, String status);

    Optional<UserGame> findByUserAndRawgGameId(User user, Integer rawgGameId);

    boolean existsByUserAndRawgGameId(User user, Integer rawgGameId);
}