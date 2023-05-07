package com.artfolio.artfolio.repository;

import com.artfolio.artfolio.dto.RealTimeAuctionInfo;
import org.springframework.data.repository.CrudRepository;


public interface RealTimeAuctionRedisRepository extends CrudRepository<RealTimeAuctionInfo, String> {
}
