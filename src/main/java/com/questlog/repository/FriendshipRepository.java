package com.questlog.repository;

import com.questlog.model.entity.Friendship;
import com.questlog.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {

    // Get all accepted friends (both directions)
    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.requester = :user OR f.receiver = :user) " +
            "AND f.status = 'ACCEPTED'")
    List<Friendship> findAcceptedFriendships(@Param("user") User user);

    // Get pending requests received by user
    List<Friendship> findByReceiverAndStatus(User receiver, String status);

    // Get pending requests sent by user
    List<Friendship> findByRequesterAndStatus(User requester, String status);

    // Check if friendship exists between two users (either direction)
    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.requester = :user1 AND f.receiver = :user2) OR " +
            "(f.requester = :user2 AND f.receiver = :user1)")
    Optional<Friendship> findBetweenUsers(
            @Param("user1") User user1,
            @Param("user2") User user2);
}