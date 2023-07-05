package com.artfolio.artfolio.business.repository;

import com.artfolio.artfolio.business.domain.Auction;
import com.artfolio.artfolio.business.domain.UserAuction;
import com.artfolio.artfolio.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuctionRepository extends JpaRepository<UserAuction, Long> {
    Optional<UserAuction> findByUserAndAuction(User user, Auction auction);
}
