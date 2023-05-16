package com.artfolio.artfolio.repository;

import com.artfolio.artfolio.domain.redis.AuctionBidInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BidRedisRepository extends CrudRepository<AuctionBidInfo, String> {
    List<AuctionBidInfo> findByAuctionKey(String auctionKey);
}
