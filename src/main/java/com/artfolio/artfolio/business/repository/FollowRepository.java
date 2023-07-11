package com.artfolio.artfolio.business.repository;

import com.artfolio.artfolio.user.entity.Follow;
import com.artfolio.artfolio.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    @Query("select f from Follow f where f.fromUser = :fromUser and f.toUser = :toUser")
    Optional<Follow> findFollow(User fromUser, User toUser);
}
