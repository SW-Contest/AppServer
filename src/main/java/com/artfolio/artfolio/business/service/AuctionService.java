package com.artfolio.artfolio.business.service;

import com.amazonaws.services.rekognition.model.Label;
import com.artfolio.artfolio.business.domain.*;
import com.artfolio.artfolio.user.dto.UserDto;
import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.business.domain.AuctionBidInfo;
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
    private final BidRedisRepository bidderRedisRepository;
    private final UserAuctionRepository userAuctionRepository;
    private final AIRedisRepository aiRedisRepository;

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
                .build();

        String uuid = auctionRepository.save(auction).getAuctionUuId();
        return CreateAuction.Res.of(uuid);
    }

    @Transactional(readOnly = true)
    public AuctionDto.DetailInfoRes getAuction(String auctionKey) {
        // 경매 정보 찾아오기
        Auction auction = auctionRepository.findAuctionWithFetchJoin(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        // 작가 정보 찾아오기
        User artist = auction.getArtist();

        // 예술품 정보 찾아오기
        ArtPiece artPiece = auction.getArtPiece();
        List<ArtPiecePhoto> artPiecePhotos = artPiece.getArtPiecePhotos();

        // 입찰자 목록 가져오기
        List<AuctionBidInfo> bidInfos = bidderRedisRepository.findByAuctionKey(auctionKey);

        // 응답 dto 만들어서 반환하기
        if (!artPiecePhotos.isEmpty()) {
            Optional<AIInfo> aiInfoOp = aiRedisRepository.findById(artPiece.getId());

            // 레디스에 캐싱된 정보가 있으면 꺼내오기
            if (aiInfoOp.isPresent()) {
                AIInfo aiInfo = aiInfoOp.get();
                AuctionDto.AIInfo of = AuctionDto.AIInfo.of(aiInfo.getLabels(), aiInfo.getContent(), aiInfo.getVoice());
                log.info("Redis aiInfo 추출 : {}", aiInfo);
                return AuctionDto.DetailInfoRes.of(auction, bidInfos, artPiecePhotos, artist, artPiece, of);
            }
        }

        return AuctionDto.DetailInfoRes.of(auction, bidInfos, artPiecePhotos, artist, artPiece, null);
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
                if (orderType == OrderType.ASC) auctions = auctionRepository.findAllByIsFinishFalseOrderByLikesAsc(pageable);
                else auctions = auctionRepository.findAllByIsFinishFalseOrderByLikesDesc(pageable);
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
    public synchronized AuctionBidDto.Res updatePrice(Principal principal, AuctionBidDto.Req req) {
        String auctionKey = req.getAuctionId();
        Long price = req.getPrice();
        Long bidderId = req.getBidderId();

        Auction auction = auctionRepository.findAuctionWithFetchJoin(auctionKey)
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
        return AuctionBidDto.Res.of(bidInfo);
    }

    @Transactional
    public Integer updateLike(String auctionKey, Long userId) {
        // 경매 정보 찾기
        Auction auction = auctionRepository.findAuctionWithFetchJoin(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        // 유저 정보 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Optional<UserAuction> userAuction = userAuctionRepository.findByUserAndAuction(user, auction);

        if (userAuction.isPresent()) {
            UserAuction ua = userAuction.get();
            if (ua.getIsLiked()) auction.decreaseLike(ua);
            else auction.increaseLike(ua);
        } else {
            UserAuction ua = new UserAuction(user, auction);
            userAuctionRepository.save(ua);
            auction.updateUserAuction(ua);
            auction.increaseLike(ua);
        }

        return auctionRepository.saveAndFlush(auction).getLikes();
    }

    @Transactional
    public Long deleteAuction(String auctionKey, Long artistId) {
        Auction auction = auctionRepository.findByAuctionUuId(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        User user = auction.getArtist();

        if (Objects.equals(user.getId(), artistId)) {
            List<String> bidIDs = bidderRedisRepository.findByAuctionKey(auctionKey)
                            .stream()
                            .map(AuctionBidInfo::getId)
                            .toList();

            bidderRedisRepository.deleteAllById(bidIDs);
            auctionRepository.deleteById(auction.getId());

            return 1L;
        }

        return 0L;
    }

    public AuctionDto.SearchResultRes searchAuction(String keyword) {
        List<Auction> allBySearch = auctionRepository.findAllBySearch(keyword);
        return AuctionDto.SearchResultRes.of(allBySearch);
    }

    @Transactional(readOnly = true)
    public UserDto.LikeUsersRes getLikeUserList(String auctionKey) {
        List<User> users = userAuctionRepository.findByAuction_AuctionUuIdAndIsLikedTrue(auctionKey)
                .stream()
                .map(UserAuction::getUser)
                .toList();

        return UserDto.LikeUsersRes.of(users);
    }
}
