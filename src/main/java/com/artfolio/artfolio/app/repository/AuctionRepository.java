package com.artfolio.artfolio.app.repository;

import com.artfolio.artfolio.app.domain.Auction;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
