package com.artfolio.artfolio.repository;

import com.artfolio.artfolio.domain.Auction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    @EntityGraph(attributePaths = { "artPiece", "artPiece.creator" })
    Optional<Auction> findAuctionById(Long id);
}
