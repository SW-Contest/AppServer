package com.artfolio.artfolio.user.service;

import com.artfolio.artfolio.business.domain.ArtPiecePhoto;
import com.artfolio.artfolio.business.domain.Auction;
import com.artfolio.artfolio.business.domain.redis.AuctionBidInfo;
import com.artfolio.artfolio.business.dto.ArtPieceDto;
import com.artfolio.artfolio.business.dto.AuctionDto;
import com.artfolio.artfolio.business.repository.AuctionRepository;
import com.artfolio.artfolio.business.repository.BidRedisRepository;
import com.artfolio.artfolio.global.exception.AuctionNotFoundException;
import com.artfolio.artfolio.global.exception.UserNotFoundException;
import com.artfolio.artfolio.user.dto.Role;
import com.artfolio.artfolio.user.dto.SocialType;
import com.artfolio.artfolio.user.dto.UserSignUpDto;
import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final BidRedisRepository bidRedisRepository;
    private final PasswordEncoder passwordEncoder;

    public Long signUp(UserSignUpDto userSignUpDto) throws Exception {
        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new Exception("이미 존재하는 이메일");
        }

        if (userRepository.findByNickname(userSignUpDto.getNickname()).isPresent()) {
            throw new Exception("이미 존재하는 닉네임");
        }

        User user = User.builder()
                .email(userSignUpDto.getEmail())
                .password(userSignUpDto.getPassword())
                .nickname(userSignUpDto.getNickname())
                .profilePhoto(userSignUpDto.getProfilePhoto())
                .role(Role.USER)
                .socialType(SocialType.NAVER)
                .socialId(userSignUpDto.getSocialId())
                .content(userSignUpDto.getContent())
                .build();

        user.passwordEncode(passwordEncoder);
        return userRepository.save(user).getId();
    }

    /*
    @Transactional(readOnly = true)
    public AuctionDto.UserLiveAuctionListRes getLiveAuctionList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<AuctionBidInfo> bidInfos = bidRedisRepository.findByBidderId(user.getId());

        List<String> auctionKeys = bidInfos
                .stream()
                .map(AuctionBidInfo::getAuctionKey)
                .toList();

        List<AuctionDto.UserAuctionInfo> result = new ArrayList<>();

        for (int i = 0; i < bidInfos.size(); i++) {
            String key = auctionKeys.get(i);
            AuctionBidInfo bidInfo = bidInfos.get(i);

            Auction auction = auctionRepository.findByAuctionUuId(key)
                    .orElseThrow(() -> new AuctionNotFoundException(key));

            if (auction.getIsFinish()) continue;

            List<String> photoPaths = auction.getArtPiece().getArtPiecePhotos()
                    .stream()
                    .map(ArtPiecePhoto::getFilePath)
                    .toList();

            result.add(AuctionDto.UserAuctionInfo.of(auction, photoPaths, bidInfo));
        }

        result.sort(Comparator.comparing(AuctionDto.UserAuctionInfo::getCreatedAt));

        return AuctionDto.UserLiveAuctionListRes.of(result);
    }

     */

    @Transactional(readOnly = true)
    public void getFinishAuctionList(Long userId) {

    }

    @Transactional(readOnly = true)
    public ArtPieceDto.UserArtPieceListRes getArtPieceList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return ArtPieceDto.UserArtPieceListRes.of(user);
    }
}
