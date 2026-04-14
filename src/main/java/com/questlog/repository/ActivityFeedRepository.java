package com.questlog.repository;

import com.questlog.model.entity.ActivityFeed;
import com.questlog.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityFeedRepository extends JpaRepository<ActivityFeed, UUID> {

    // Get activity for a specific user
    List<ActivityFeed> findByUserOrderByCreatedAtDesc(User user);

    // Get activity from a list of users (for friends feed)
    @Query("SELECT a FROM ActivityFeed a WHERE a.user IN :users " +
            "ORDER BY a.createdAt DESC")
    List<ActivityFeed> findByUsersOrderByCreatedAtDesc(
            @Param("users") List<User> users);
}