package com.artfolio.artfolio.repository;

import com.artfolio.artfolio.domain.Auction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    /*
    --> EntityGraph 대체
    @Query("select a, p, m " +
           "from Auction a " +
           "join fetch ArtPiece p on a.artPiece = p " +
           "join fetch Member m on p.creator = m " +
           "where a.id = :auctionId")
     */

    @EntityGraph(attributePaths = { "artPiece", "member" })
    Optional<Auction> findById(Long id);
}
