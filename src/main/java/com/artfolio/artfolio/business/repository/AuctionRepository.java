package com.artfolio.artfolio.business.repository;

import com.artfolio.artfolio.business.domain.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Optional<Auction> findByAuctionUuId(String uuid);
}
