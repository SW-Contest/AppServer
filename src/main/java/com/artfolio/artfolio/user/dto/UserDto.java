package com.artfolio.artfolio.user.dto;

import com.artfolio.artfolio.business.domain.*;
import com.artfolio.artfolio.business.dto.ArtPieceDto;
import com.artfolio.artfolio.business.dto.AuctionDto;
import com.artfolio.artfolio.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class UserDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class SignUpReq {
        private String email;
        private String nickname;
        private String profilePhoto;
        private String content;

        public User toEntity() {
            return User.builder()
                    .email(email)
                    .nickname(nickname)
                    .profilePhoto(profilePhoto)
                    .content(content)
                    .role(Role.TEST)
                    .socialType(SocialType.TEST)
                    .refreshToken(null)
                    .socialId(null)
                    .build();
        }
    }

    @Getter @Setter @ToString @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserAttendingAuctionListRes {
        private AuctionDto.ArtistInfo userInfo;
        private List<UserAttendingAuction> userAttendingAuctionList;

        public static UserAttendingAuctionListRes of(User user, List<Auction> list, List<AuctionBidInfo> auctionBidInfos) {
            List<UserAttendingAuction> userAttendingAuctions = new ArrayList<>();

            for (int i = 0; i < auctionBidInfos.size(); i++) {
                AuctionBidInfo bidInfo = auctionBidInfos.get(i);
                Long bidPrice = bidInfo.getBidPrice();
                LocalDateTime bidDate = bidInfo.getBidDate();
                Auction auction = list.get(i);

                UserAttendingAuction userAttendingAuction = UserAttendingAuction.of(auction, bidPrice, bidDate);

                if (userAttendingAuctions.contains(userAttendingAuction)) {
                    Optional<UserAttendingAuction> first = userAttendingAuctions
                            .stream()
                            .filter(ua -> Objects.equals(ua.getAuctionUuId(), userAttendingAuction.auctionUuId))
                            .findFirst();

                    if (first.isPresent()) {
                        UserAttendingAuction prev = first.get();

                        if (prev.getBidPrice() > userAttendingAuction.getBidPrice()) continue;
                        else {
                            userAttendingAuctions.remove(prev);
                            userAttendingAuctions.add(userAttendingAuction);
                        }
                    }
                }

                else {
                    userAttendingAuctions.add(userAttendingAuction);
                }
            }

            return UserAttendingAuctionListRes.builder()
                    .userInfo(AuctionDto.ArtistInfo.of(user))
                    .userAttendingAuctionList(userAttendingAuctions)
                    .build();
        }
    }

    @Getter @Setter @Builder @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    private static class UserAttendingAuction {
        private String auctionUuId;
        private Long bidPrice;
        private LocalDateTime bidDate;
        private AuctionDto.ArtistInfo artistInfo;
        private AuctionDto.AuctionInfo auctionInfo;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserAttendingAuction that = (UserAttendingAuction) o;
            return Objects.equals(auctionUuId, that.auctionUuId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(auctionUuId);
        }

        public static UserAttendingAuction of(Auction auction, Long bidPrice, LocalDateTime bidDate) {
            List<String> paths = auction.getArtPiece()
                    .getArtPiecePhotos()
                    .stream()
                    .map(ArtPiecePhoto::getFilePath)
                    .toList();

            User artist = auction.getArtist();

            return UserAttendingAuction.builder()
                    .auctionUuId(auction.getAuctionUuId())
                    .bidPrice(bidPrice)
                    .bidDate(bidDate)
                    .artistInfo(AuctionDto.ArtistInfo.of(artist))
                    .auctionInfo(AuctionDto.AuctionInfo.of(auction, paths))
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
        private List<ArtPieceDto.ArtPieceInfoRes> artPieceInfos;

        public static UserLikeArtPiecesRes of(List<ArtPiece> artPieces) {
            List<ArtPieceDto.ArtPieceInfoRes> list = artPieces.stream()
                    .map(ArtPieceDto.ArtPieceInfoRes::of)
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
        private String email;
        private String name;
        private String content;
        private String photoPath;

        public static UserInfo of(User user) {
            return UserInfo.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getNickname())
                    .content(user.getContent())
                    .photoPath(user.getProfilePhoto())
                    .build();
        }
    }
}
