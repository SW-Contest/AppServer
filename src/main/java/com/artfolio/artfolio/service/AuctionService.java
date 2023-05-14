package com.artfolio.artfolio.service;

import com.artfolio.artfolio.domain.ArtPiece;
import com.artfolio.artfolio.domain.Auction;
import com.artfolio.artfolio.domain.Member;
import com.artfolio.artfolio.dto.RealTimeAuctionInfo;
import com.artfolio.artfolio.exception.*;
import com.artfolio.artfolio.repository.ArtPieceRepository;
import com.artfolio.artfolio.repository.AuctionRepository;
import com.artfolio.artfolio.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Transactional
@RequiredArgsConstructor
@Service
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final MemberRepository memberRepository;
    private final ArtPieceRepository artPieceRepository;

    /*
    /* 종료된 경매 기록 DB에 저장 */
    public Long saveAuctionInfo(RealTimeAuctionInfo auctionInfo, Boolean isSold) {
        Long artPieceId = auctionInfo.getArtPieceId();
        Long bidderId = auctionInfo.getBidderId();
        Long artistId = auctionInfo.getArtistInfo().getMemberId();

        /* 팔렸는데 낙찰자 ID가 누락된 요청은 비정상 요청이므로 종료 */
        if (isSold && bidderId == null) return 0L;

        /* 팔리지 않았는데 낙찰자 ID가 포함된 요청은 비정상 요청이므로 종료 */
        if (!isSold && bidderId != null) return 0L;

        /* 관련 엔티티들을 가져온다 */
        Member artist = memberRepository.findById(artistId)
                .orElseThrow(() -> new MemberNotFoundException(artistId));

        Member bidder = !isSold ? null : memberRepository.findById(auctionInfo.getBidderId())
                .orElseThrow(() -> new MemberNotFoundException(bidderId));

        ArtPiece artPiece = artPieceRepository.findById(artPieceId)
                .orElseThrow(() -> new ArtPieceNotFoundException(artPieceId));

        /* artPiece에 저장된 id와 전달받은 artist id가 다르면 예외 상황이므로 종료 */
        if (!Objects.equals(artPiece.getArtist().getId(), artistId)) return 0L;

        /* 모든 예외 상황을 통과했으면 DB에 경매 정보 저장 */
        Auction auction = Auction.builder()
                .artist(artist)
                .startPrice(auctionInfo.getAuctionStartPrice())
                .finalPrice(auctionInfo.getAuctionCurrentPrice())
                .like(auctionInfo.getLike())
                .bidder(bidder)
                .artPiece(artPiece)
                .isSold(isSold)
                .build();

        auctionRepository.save(auction);
        return 1L;
    }
}
