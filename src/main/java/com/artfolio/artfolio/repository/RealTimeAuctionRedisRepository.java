package com.artfolio.artfolio.repository;

import com.artfolio.artfolio.dto.RealTimeAuctionInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface RealTimeAuctionRedisRepository extends CrudRepository<RealTimeAuctionInfo, String> {
    List<RealTimeAuctionInfo> findAll(Pageable pageable);
}
