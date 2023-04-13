package com.artfolio.artfolio.repository;

import com.artfolio.artfolio.domain.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    @Query("select a, p, m " +
           "from Auction a " +
           "join fetch ArtPiece p on a.artPiece = p " +
           "join fetch Member m on p.creator = m " +
           "where a.id = :auctionId")
    Optional<Auction> findByAuctionIdWithFetch(@Param("auctionId") Long auctionId);
}
