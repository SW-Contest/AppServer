package com.artfolio.artfolio.repository;

import com.artfolio.artfolio.domain.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
