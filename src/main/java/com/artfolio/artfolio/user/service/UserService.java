package com.artfolio.artfolio.user.service;

import com.artfolio.artfolio.business.domain.*;
import com.artfolio.artfolio.business.dto.ArtPieceDto;
import com.artfolio.artfolio.business.repository.AuctionRepository;
import com.artfolio.artfolio.business.repository.BidRedisRepository;
import com.artfolio.artfolio.business.repository.UserArtPieceRepository;
import com.artfolio.artfolio.business.repository.UserAuctionRepository;
import com.artfolio.artfolio.global.exception.UserNotFoundException;
import com.artfolio.artfolio.user.dto.UserDto;
import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final BidRedisRepository bidRedisRepository;
    private final UserArtPieceRepository userArtPieceRepository;
    private final UserAuctionRepository userAuctionRepository;

    public Long testUserSignUp(UserDto.SignUpReq req) {
        User user = req.toEntity();
        return userRepository.save(user).getId();
    }

    @Transactional(readOnly = true)
    public UserDto.UserAttendingAuctionListRes getLiveAuctionList(Long userId) {
        // 1. userId로 입찰 목록 검색
        // 2. 가져온 입찰 목록으로 경매 검색
        /*
        List<Auction> auctions = bidRedisRepository.findByBidderId(userId)
                .stream()
                .map(info -> auctionRepository.findByAuctionUuIdWithFetch(info.getAuctionKey()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
         */

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<AuctionBidInfo> auctionBidInfos = bidRedisRepository.findByBidderId(userId);

        List<Auction> list = auctionBidInfos
                .stream()
                .map(bidInfo -> auctionRepository.findByAuctionUuIdWithFetch(bidInfo.getAuctionKey()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        return UserDto.UserAttendingAuctionListRes.of(user, list, auctionBidInfos);
    }

    @Transactional(readOnly = true)
    public UserDto.UserBidAuctionListRes getBidAuctionList(Long userId) {
        // auction 엔티티의 isFinish가 True이고, lastBidder가 userId와 같은 경매 목록 반환
        List<Auction> allByBidder = auctionRepository.findAllByBidder(userId);
        return UserDto.UserBidAuctionListRes.of(allByBidder);
    }

    @Transactional(readOnly = true)
    public ArtPieceDto.UserArtPieceListRes getArtPieceList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return ArtPieceDto.UserArtPieceListRes.of(user);
    }

    @Transactional(readOnly = true)
    public UserDto.UserLikeArtPiecesRes getLikeArtPieces(Long userId) {
        List<ArtPiece> artPieces = userArtPieceRepository.findAllUserLikes(userId)
                .stream()
                .map(UserArtPiece::getArtPiece)
                .toList();

        return UserDto.UserLikeArtPiecesRes.of(artPieces);
    }

    @Transactional(readOnly = true)
    public UserDto.UserLikeAuctionsRes getLikeAuctions(Long userId) {
        List<Auction> auctions = userAuctionRepository.findAllUserLikes(userId)
                .stream()
                .map(UserAuction::getAuction)
                .toList();

        return UserDto.UserLikeAuctionsRes.of(auctions);
    }

    public Long updateUserContent(Long userId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.updateContent(content);

        return 1L;
    }

    public UserDto.UserInfo getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return UserDto.UserInfo.of(user);
    }
}
