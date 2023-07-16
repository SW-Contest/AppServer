package com.artfolio.artfolio.user.dto;

import com.artfolio.artfolio.business.domain.ArtPiecePhoto;
import com.artfolio.artfolio.business.domain.Auction;
import com.artfolio.artfolio.business.domain.AuctionBidInfo;
import com.artfolio.artfolio.business.dto.AuctionDto;
import com.artfolio.artfolio.user.entity.User;
import lombok.*;

import java.util.List;


public class UserDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class SignUpReq {
        private String email;
        private String password;
        private String nickname;
        private String profilePhoto;
        private Role role;
        private SocialType socialType;
        private String socialId;
        private String content;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class OAuth2LoginInfoRes {
        private Long userId;
        private String socialId;
        private String profileImage;
        private String email;
        private String name;
        private String socialType;
        private String role;

        public static OAuth2LoginInfoRes of(User user) {
            return OAuth2LoginInfoRes
                    .builder()
                    .userId(user.getId())
                    .socialId(user.getSocialId())
                    .socialType(user.getSocialType().name())
                    .email(user.getEmail())
                    .name(user.getNickname())
                    .profileImage(user.getProfilePhoto())
                    .role(user.getRole().name())
                    .build();
        }
    }

    @Getter @Setter @ToString @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserBidAuctionListRes {
        private Integer size;
        private List<UserBidAuctionList> userBidAuctionList;

        public static UserBidAuctionListRes of(List<Auction> auctions) {
            List<UserBidAuctionList> list = auctions.stream()
                    .map(UserBidAuctionList::of)
                    .toList();

            return UserBidAuctionListRes.builder()
                    .size(auctions.size())
                    .userBidAuctionList(list)
                    .build();
        }
    }

    @Getter @Setter @Builder @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    private static class UserBidAuctionList {
        private AuctionDto.ArtistInfo artistInfo;
        private AuctionDto.AuctionInfo auctionInfo;

        public static UserBidAuctionList of(Auction auction) {
            List<String> paths = auction.getArtPiece()
                    .getArtPiecePhotos()
                    .stream()
                    .map(ArtPiecePhoto::getFilePath)
                    .toList();

            User artist = auction.getArtist();

            return UserBidAuctionList.builder()
                    .artistInfo(AuctionDto.ArtistInfo.of(artist))
                    .auctionInfo(AuctionDto.AuctionInfo.of(auction, paths))
                    .build();
        }
    }
}
