package com.artfolio.artfolio.business.repository;

import com.artfolio.artfolio.business.domain.Auction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface AuctionRepository extends JpaRepository<Auction, Long> {
    @Query("select a from Auction a " +
            "join fetch a.artist " +
            "join fetch a.artPiece " +
            "where a.auctionUuId = :uuid and a.isFinish = false")
    Optional<Auction> findAuctionWithFetchJoin(String uuid);

    @Query("select a from Auction a " +
            "join fetch a.artist " +
            "join fetch a.artPiece " +
            "where a.isFinish = false " +
            "and " +
            "(a.title like %:keyword% or a.artist.nickname like %:keyword% or a.artPiece.title like %:keyword%)")
    List<Auction> findAllBySearch(String keyword);

    @Query("select a from Auction a " +
            "join fetch a.bidder " +
            "join fetch a.artist " +
            "join fetch a.artPiece " +
            "where a.isFinish = true " +
            "and " +
            "a.bidder.id = :userId")
    List<Auction> findAllByBidder(Long userId);

    @Query("select a from Auction a " +
            "join fetch a.artPiece " +
            "join fetch a.artist " +
            "join fetch a.bidder " +
            "where a.isFinish = false " +
            "and " +
            "a.auctionUuId = :uuid")
    Optional<Auction> findByAuctionUuIdWithFetch(String uuid);

    Optional<Auction> findByAuctionUuId(String uuid);
    Slice<Auction> findAllByIsFinishFalseOrderByCurrentPriceAsc(Pageable pageable);
    Slice<Auction> findAllByIsFinishFalseOrderByCurrentPriceDesc(Pageable pageable);
    Slice<Auction> findAllByIsFinishFalseOrderByLikesAsc(Pageable pageable);
    Slice<Auction> findAllByIsFinishFalseOrderByLikesDesc(Pageable pageable);
    Slice<Auction> findAllByIsFinishFalseOrderByCreatedAtAsc(Pageable pageable);
    Slice<Auction> findAllByIsFinishFalseOrderByCreatedAtDesc(Pageable pageable);
}
