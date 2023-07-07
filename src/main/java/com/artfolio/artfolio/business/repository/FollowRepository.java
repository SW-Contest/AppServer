package com.artfolio.artfolio.business.repository;

import com.artfolio.artfolio.business.domain.Follow;
import com.artfolio.artfolio.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollower(User user);
    Optional<Follow> findByFollowing(User user);
}
