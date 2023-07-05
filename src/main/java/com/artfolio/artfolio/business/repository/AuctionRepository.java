package com.artfolio.artfolio.business.repository;

import com.artfolio.artfolio.business.domain.Auction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Optional<Auction> findByAuctionUuId(String uuid);
    Slice<Auction> findAllByIsFinishFalseOrderByCurrentPriceAsc(Pageable pageable);
    Slice<Auction> findAllByIsFinishFalseOrderByCurrentPriceDesc(Pageable pageable);
    Slice<Auction> findAllByIsFinishFalseOrderByLikesAsc(Pageable pageable);
    Slice<Auction> findAllByIsFinishFalseOrderByLikesDesc(Pageable pageable);
    Slice<Auction> findAllByIsFinishFalseOrderByCreatedAtAsc(Pageable pageable);
    Slice<Auction> findAllByIsFinishFalseOrderByCreatedAtDesc(Pageable pageable);
}
