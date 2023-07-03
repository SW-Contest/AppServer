package com.artfolio.artfolio.business.repository;

import com.artfolio.artfolio.business.domain.Auction;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
