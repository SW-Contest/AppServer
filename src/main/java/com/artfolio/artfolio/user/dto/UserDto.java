package com.artfolio.artfolio.user.dto;

import com.artfolio.artfolio.business.domain.*;
import com.artfolio.artfolio.business.dto.ArtPieceDto;
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

    @Getter @Setter @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LikeUsersRes {
        private Integer size;
        private List<AuctionDto.ArtistInfo> likeUsers;

        public static LikeUsersRes of(List<User> users) {
            List<AuctionDto.ArtistInfo> likeUsers = users.stream()
                    .map(AuctionDto.ArtistInfo::of)
                    .toList();

            return LikeUsersRes.builder()
                    .size(users.size())
                    .likeUsers(likeUsers)
                    .build();
        }
    }

    @Getter @Setter @ToString @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLikeArtPiecesRes {
        private Integer size;
        private List<ArtPieceDto.ArtPieceInfo> artPieceInfos;

        public static UserLikeArtPiecesRes of(List<ArtPiece> artPieces) {
            List<ArtPieceDto.ArtPieceInfo> list = artPieces.stream()
                    .map(piece -> ArtPieceDto.ArtPieceInfo.of(
                            piece,
                            piece.getArtPiecePhotos()
                                    .stream()
                                    .map(ArtPiecePhoto::getFilePath)
                                    .toList()
                    ))
                    .toList();

            return UserLikeArtPiecesRes.builder()
                    .size(artPieces.size())
                    .artPieceInfos(list)
                    .build();
        }
    }

    @Getter @Setter @ToString @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAuctionArtistInfo {
        private AuctionDto.AuctionInfo auctionInfo;
        private AuctionDto.ArtistInfo artistInfo;

        public static UserAuctionArtistInfo of(User artist, AuctionDto.AuctionInfo auctionInfo) {
            return UserAuctionArtistInfo.builder()
                    .auctionInfo(auctionInfo)
                    .artistInfo(AuctionDto.ArtistInfo.of(artist))
                    .build();
        }
    }

    @Getter @Setter @ToString @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLikeAuctionsRes {
        private Integer size;
        private List<UserAuctionArtistInfo> auctionInfos;

        public static UserLikeAuctionsRes of(List<Auction> auctions) {
            List<UserAuctionArtistInfo> list = auctions.stream()
                    .map(auction -> UserAuctionArtistInfo.of(
                            auction.getArtist(),
                            AuctionDto.AuctionInfo.of(
                                    auction,
                                    auction.getArtPiece().getArtPiecePhotos()
                                            .stream()
                                            .map(ArtPiecePhoto::getFilePath)
                                            .toList()
                            )
                    ))
                    .toList();

            return UserLikeAuctionsRes.builder()
                    .size(auctions.size())
                    .auctionInfos(list)
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String name;
        private String content;
        private String photoPath;

        public static UserInfo of(User user) {
            return UserInfo.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .name(user.getNickname())
                    .content(user.getContent())
                    .photoPath(user.getProfilePhoto())
                    .build();
        }
    }
}
