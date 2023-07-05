package com.artfolio.artfolio.business.service;

import com.artfolio.artfolio.business.domain.*;
import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.business.domain.redis.AuctionBidInfo;
import com.artfolio.artfolio.business.dto.*;
import com.artfolio.artfolio.business.repository.*;
import com.artfolio.artfolio.global.exception.*;
import com.artfolio.artfolio.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;



@Slf4j
@RequiredArgsConstructor
@Service
public class AuctionService {
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final ArtPieceRepository artPieceRepository;
    private final ArtPiecePhotoRepository artPiecePhotoRepository;
    private final BidRedisRepository bidderRedisRepository;

    @Transactional
    public CreateAuction.Res createAuction(CreateAuction.Req req) {
        // 1. 아티스트 엔티티와 아트피스 엔티티를 찾아온다.
        Long artPieceId = req.getArtPieceId();
        ArtPiece artPiece = artPieceRepository.findById(artPieceId)
                .orElseThrow(() -> new ArtPieceNotFoundException(artPieceId));

        Long artistId = req.getArtistId();
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new UserNotFoundException(artistId));

        if (!artPiece.getAuctions().isEmpty()) {
            throw new AuctionAlreadyExistsException(artPieceId);
        }

        Auction auction = Auction.builder()
                .artist(artist)
                .artPiece(artPiece)
                .title(req.getAuctionTitle())
                .content(req.getAuctionContent())
                .startPrice(req.getAuctionStartPrice())
                .currentPrice(req.getAuctionStartPrice())
                .like(0)
                .build();

        String uuid = auctionRepository.save(auction).getAuctionUuId();
        return CreateAuction.Res.of(uuid);
    }

    @Transactional(readOnly = true)
    public AuctionDto.DetailInfoRes getAuction(String auctionKey) {
        // 경매 정보 찾아오기
        Auction auction = auctionRepository.findByAuctionUuId(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        // 작가 정보 찾아오기
        User artist = auction.getArtist();

        // 예술품 정보 찾아오기
        ArtPiece artPiece = auction.getArtPiece();
        List<ArtPiecePhoto> artPiecePhotos = artPiecePhotoRepository.findArtPiecePhotoByArtPiece_Id(artPiece.getId());

        // 입찰자 목록 가져오기
        List<AuctionBidInfo> bidInfos = bidderRedisRepository.findByAuctionKey(auctionKey);

        // 응답 dto 만들어서 반환하기
        return AuctionDto.DetailInfoRes.of(auction, bidInfos, artPiecePhotos, artist, artPiece);
    }

    @Transactional(readOnly = true)
    public AuctionDto.PreviewInfoRes getAuctionList(SearchType searchType, OrderType orderType, Pageable pageable) {
        log.info("getAuctionList() 실행! searchType : {}, orderType : {}", searchType, orderType);

        Slice<Auction> auctions;

        switch (searchType) {
            case CURRENT_PRICE -> {
                if (orderType == OrderType.ASC) auctions = auctionRepository.findAllByIsFinishFalseOrderByCurrentPriceAsc(pageable);
                else auctions = auctionRepository.findAllByIsFinishFalseOrderByCurrentPriceDesc(pageable);
            }
            case LIKE -> {
                if (orderType == OrderType.ASC) auctions = auctionRepository.findAllByIsFinishFalseOrderByLikeAsc(pageable);
                else auctions = auctionRepository.findAllByIsFinishFalseOrderByLikeDesc(pageable);
            }
            case CREATED_AT -> {
                if (orderType == OrderType.ASC) auctions = auctionRepository.findAllByIsFinishFalseOrderByCreatedAtAsc(pageable);
                else auctions = auctionRepository.findAllByIsFinishFalseOrderByCreatedAtDesc(pageable);
            }
            default -> {
                auctions = auctionRepository.findAll(pageable);
            }
        }

        return AuctionDto.PreviewInfoRes.of(auctions);
    }

    @Transactional
    public synchronized AuctionBid.Res updatePrice(Principal principal, AuctionBid.Req req) {
        String auctionKey = req.getAuctionId();
        Long price = req.getPrice();
        Long bidderId = req.getBidderId();

        Auction auction = auctionRepository.findByAuctionUuId(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        // 현재가보다 낮은 경우 예외 처리
        if (auction.getCurrentPrice() >= price) {
            throw new InvalidBidPriceException(principal, auction.getCurrentPrice(), price, auctionKey);
        }

        // 입찰자 정보를 DB에서 찾아온 뒤 DTO 생성
        User bidder = userRepository.findById(bidderId)
                .orElseThrow(() -> new UserNotFoundException(bidderId, principal));

        // redis 입찰 기록 저장
        AuctionBidInfo bidInfo = bidderRedisRepository.save(AuctionBidInfo.of(bidder, auctionKey, price));

        // 경매 정보 업데이트
        auction.updateLastBidder(bidder);
        auction.updateCurrentPrice(price);
        auctionRepository.save(auction);

        // 응답 객체를 만들어 반환
        return AuctionBid.Res.of(bidInfo);
    }

    @Transactional
    public Integer updateLike(String auctionKey, Long userId) {
        // 멤버 정보 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 경매 정보 찾기
        Auction auction = auctionRepository.findByAuctionUuId(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        auction.updateLike(user);

        return auctionRepository.saveAndFlush(auction).getLike();
    }

    @Transactional
    public Long deleteAuction(String auctionKey, Long artistId) {
        User user = userRepository.findById(artistId)
                .orElseThrow(() -> new UserNotFoundException(artistId));

        Auction auction = auctionRepository.findByAuctionUuId(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        if (Objects.equals(auction.getArtist().getId(), user.getId())) {
            List<String> bidIDs = bidderRedisRepository.findByAuctionKey(auctionKey)
                            .stream()
                            .map(AuctionBidInfo::getId)
                            .toList();

            bidderRedisRepository.deleteAllById(bidIDs);
            auctionRepository.delete(auction);

            return 1L;
        }

        return 0L;
    }
}
