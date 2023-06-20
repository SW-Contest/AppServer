package com.artfolio.artfolio.business.service;

import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.business.domain.redis.RealTimeAuctionInfo;
import com.artfolio.artfolio.business.repository.ArtPieceRepository;
import com.artfolio.artfolio.business.repository.AuctionRepository;
import com.artfolio.artfolio.user.repository.UserRepository;
import com.artfolio.artfolio.business.domain.ArtPiece;
import com.artfolio.artfolio.business.domain.Auction;
import com.artfolio.artfolio.global.exception.ArtPieceNotFoundException;
import com.artfolio.artfolio.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Transactional
@RequiredArgsConstructor
@Service
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final UserRepository memberRepository;
    private final ArtPieceRepository artPieceRepository;

    /* 종료된 경매 기록 DB에 저장 */
    public Long saveAuctionInfo(RealTimeAuctionInfo auctionInfo, Boolean isSold, Long bidderId) {
        Long artPieceId = auctionInfo.getArtPieceId();
        Long artistId = auctionInfo.getArtistId();

        // 팔렸는데 낙찰자 ID가 누락된 요청은 비정상 요청이므로 종료
        if (isSold && bidderId == null) return 0L;

        // 팔리지 않았는데 낙찰자 ID가 포함된 요청은 비정상 요청이므로 종료
        if (!isSold && bidderId != null) return 0L;

        // 관련 엔티티들을 가져온다
        User artist = memberRepository.findById(artistId)
                .orElseThrow(() -> new UserNotFoundException(artistId));

        User bidder = !isSold ? null : memberRepository.findById(bidderId)
                .orElseThrow(() -> new UserNotFoundException(bidderId));

        ArtPiece artPiece = artPieceRepository.findById(artPieceId)
                .orElseThrow(() -> new ArtPieceNotFoundException(artPieceId));

        // artPiece에 저장된 id와 전달받은 artist id가 다르면 예외 상황이므로 종료
        if (!Objects.equals(artPiece.getArtist().getId(), artistId)) return 0L;

        // 모든 예외 상황을 통과했으면 DB에 경매 정보 저장
        Auction auction = Auction.builder()
                .artist(artist)
                .startPrice(auctionInfo.getAuctionStartPrice())
                .finalPrice(auctionInfo.getAuctionCurrentPrice())
                .like(auctionInfo.getLikeMembers().size())
                .bidder(bidder)
                .artPiece(artPiece)
                .isSold(isSold)
                .build();

        auctionRepository.save(auction);
        return 1L;
    }
}
