package com.artfolio.artfolio.business.repository;

import com.artfolio.artfolio.business.domain.redis.AuctionBidInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BidRedisRepository extends CrudRepository<AuctionBidInfo, String> {
    List<AuctionBidInfo> findByAuctionKey(String auctionKey);
    List<AuctionBidInfo> findByBidderId(Long bidderId);
}
