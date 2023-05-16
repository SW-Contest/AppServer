package com.artfolio.artfolio.repository;

import com.artfolio.artfolio.domain.redis.RealTimeAuctionInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface RealTimeAuctionRedisRepository extends CrudRepository<RealTimeAuctionInfo, String> {
    Slice<RealTimeAuctionInfo> findAll(Pageable pageable);
    Optional<RealTimeAuctionInfo> findByArtPieceId(Long artPieceId);
}
