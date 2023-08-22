package com.artfolio.artfolio.business.repository;

import com.artfolio.artfolio.business.domain.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {
}
