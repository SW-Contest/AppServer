package com.artfolio.artfolio.business.repository;

import com.artfolio.artfolio.business.domain.Auction;
import com.artfolio.artfolio.business.domain.UserAuction;
import com.artfolio.artfolio.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserAuctionRepository extends JpaRepository<UserAuction, Long> {
    Optional<UserAuction> findByUserAndAuction(User user, Auction auction);
    List<UserAuction> findByAuction_AuctionUuIdAndIsLikedTrue(String auctionKey);

    @Query("select ua from UserAuction ua " +
            "join fetch ua.auction " +
            "join fetch ua.auction.artPiece " +
            "where ua.isLiked = true " +
            "and " +
            "ua.user.id = :userId")
    List<UserAuction> findAllUserLikes(Long userId);
}
