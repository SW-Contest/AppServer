package com.artfolio.artfolio.business.dto;

import com.amazonaws.services.rekognition.model.Label;
import com.artfolio.artfolio.business.domain.ArtPiece;
import com.artfolio.artfolio.business.domain.ArtPiecePhoto;
import com.artfolio.artfolio.business.domain.Auction;
import com.artfolio.artfolio.business.domain.AuctionBidInfo;
import com.artfolio.artfolio.user.entity.User;
import lombok.*;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AuctionDto {

    /* 검색 결과를 반환해주는 DTO */
    @Getter @Setter @ToString @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearchResultRes {
        int size;
        List<SearchResult> searchResult;

        public static SearchResultRes of(List<Auction> auctions) {
            List<SearchResult> searchResult = auctions.stream()
                    .map(SearchResult::of)
                    .toList();

            return SearchResultRes.builder()
                    .size(searchResult.size())
                    .searchResult(searchResult)
                    .build();
        }

        @Getter @Setter @ToString @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        private static class SearchResult {
            private ArtistInfo artistInfo;
            private ArtPieceInfo artPieceInfo;
            private AuctionInfo auctionInfo;

            private static SearchResult of(Auction auction) {
                User artist = auction.getArtist();
                ArtPiece artPiece = auction.getArtPiece();
                List<String> paths = artPiece.getArtPiecePhotos()
                        .stream()
                        .map(ArtPiecePhoto::getFilePath)
                        .toList();

                return SearchResult.builder()
                        .artistInfo(ArtistInfo.of(artist))
                        .artPieceInfo(ArtPieceInfo.of(artPiece))
                        .auctionInfo(AuctionInfo.of(auction, paths))
                        .build();
            }
        }

    }

    @Getter @Setter @ToString @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLiveAuctionListRes {
        private Integer size;
        private List<AuctionDto.UserAuctionInfo> data;

        public static UserLiveAuctionListRes of(List<AuctionDto.UserAuctionInfo> infos, List<AuctionBidInfo> bidInfos) {
            return UserLiveAuctionListRes.builder()
                    .size(infos.size())
                    .data(infos)
                    .build();
        }
    }

    @Builder @Getter
    @AllArgsConstructor
    public static class UserAuctionInfo {
        private String id;
        private String title;
        private String content;
        private Long startPrice;
        private Long currentPrice;
        private Integer like;
        private LocalDateTime createdAt;
        private LocalDateTime finishedAt;
        private List<String> photoPaths;

        public static UserAuctionInfo of(Auction auction, List<String> photoPaths) {
            return UserAuctionInfo.builder()
                    .id(auction.getAuctionUuId())
                    .title(auction.getTitle())
                    .content(auction.getContent())
                    .startPrice(auction.getStartPrice())
                    .currentPrice(auction.getCurrentPrice())
                    .like(auction.getLikes())
                    .createdAt(auction.getCreatedAt())
                    .finishedAt(auction.getCreatedAt().plusDays(DEFAULT_AUCTION_FINISH_DAYS))
                    .photoPaths(photoPaths)
                    .build();
        }
    }

    @Getter @Setter @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PreviewInfoRes {
        private Boolean hasNext;
        private Boolean isLast;
        private Integer pageSize;
        private Integer pageNumber;
        private Integer dataSize;
        private List<PreviewInfo> data;

        public static AuctionDto.PreviewInfoRes of(Slice<Auction> auctions) {
            List<PreviewInfo> previewInfos = new ArrayList<>();

            for (Auction auction : auctions) {
                User artist = auction.getArtist();
                List<ArtPiecePhoto> artPiecePhotos = auction.getArtPiece().getArtPiecePhotos()
                        .stream()
                        .filter(ArtPiecePhoto::getIsThumbnail)
                        .toList();

                String path = "null";
                if (!artPiecePhotos.isEmpty()) path = artPiecePhotos.get(0).getFilePath();

                previewInfos.add(PreviewInfo.of(auction, artist, path));
            }

            return new PreviewInfoRes(
                    auctions.hasNext(),
                    auctions.isLast(),
                    auctions.getSize(),
                    auctions.getNumber(),
                    previewInfos.size(),
                    previewInfos
            );
        }
    }

    @Getter
    @AllArgsConstructor
    private static class PreviewInfo {
        private ArtistInfo artistInfo;
        private AuctionInfo auctionInfo;

        public static PreviewInfo of(Auction auction, User artist, String thumbnailPath) {
            ArtistInfo artistInfo = ArtistInfo.of(artist);

            AuctionInfo auctionInfo = AuctionInfo.builder()
                    .id(auction.getAuctionUuId())
                    .title(auction.getTitle())
                    .content(auction.getContent())
                    .startPrice(auction.getStartPrice())
                    .currentPrice(auction.getCurrentPrice())
                    .like(auction.getLikes())
                    .createdAt(auction.getCreatedAt())
                    .finishedAt(auction.getCreatedAt().plusDays(DEFAULT_AUCTION_FINISH_DAYS))
                    .photoPaths(List.of(thumbnailPath))
                    .build();

            return new PreviewInfo(artistInfo, auctionInfo);
        }
    }


    private static final Integer DEFAULT_AUCTION_FINISH_DAYS = 7;

    @Getter @Setter @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AIInfo {
        private List<Label> labels;
        private String content;

        public static AIInfo of(List<Label> labels, String content) {
            return AIInfo.builder()
                    .labels(labels)
                    .content(content)
                    .build();
        }
    }

    @Getter @Setter @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailInfoRes {
        private ArtistInfo artistInfo;
        private ArtPieceInfo artPieceInfo;
        private AuctionInfo auctionInfo;
        private AIInfo aiInfo;
        private List<BidderInfo> bidderInfos;

        public static DetailInfoRes of(
                Auction auction,
                List<AuctionBidInfo> bidInfo,
                List<ArtPiecePhoto> paths,
                User artist,
                ArtPiece artPiece,
                AIInfo aiInfo
        ) {
            ArtistInfo artistInfo = ArtistInfo.of(artist);

            List<String> photoPaths = paths.stream()
                    .map(ArtPiecePhoto::getFilePath)
                    .toList();

            AuctionInfo auctionInfo = AuctionInfo.of(auction, photoPaths);

            ArtPieceInfo artPieceInfo = ArtPieceInfo.of(artPiece);

            List<BidderInfo> bidderInfos = bidInfo.stream()
                    .map(BidderInfo::of)
                    .sorted(Comparator.comparing(BidderInfo::getBidDate))
                    .toList();

            return DetailInfoRes.builder()
                    .artistInfo(artistInfo)
                    .artPieceInfo(artPieceInfo)
                    .auctionInfo(auctionInfo)
                    .bidderInfos(bidderInfos)
                    .aiInfo(aiInfo)
                    .build();
        }
    }

    @Builder @Getter
    @AllArgsConstructor
    public static class ArtPieceInfo {
        private Long id;
        private String title;
        private String content;
        private Integer likes;

        public static ArtPieceInfo of(ArtPiece artPiece) {
            return ArtPieceInfo.builder()
                    .id(artPiece.getId())
                    .title(artPiece.getTitle())
                    .content(artPiece.getContent())
                    .likes(artPiece.getLikes())
                    .build();
        }
    }

    @Builder @Getter
    @AllArgsConstructor
    public static class ArtistInfo {
        private Long id;
        private String username;
        private String name;
        private String email;
        private String content;
        private String photoPath;

        public static ArtistInfo of(User artist) {
            return ArtistInfo.builder()
                    .id(artist.getId())
                    .username(artist.getUsername())
                    .name(artist.getNickname())
                    .email(artist.getEmail())
                    .photoPath(artist.getProfilePhoto())
                    .content(artist.getContent())
                    .build();
        }
    }

    @Builder @Getter
    @AllArgsConstructor
    public static class AuctionInfo {
        private String id;
        private String title;
        private String content;
        private Long startPrice;
        private Long currentPrice;
        private Integer like;
        private LocalDateTime createdAt;
        private LocalDateTime finishedAt;
        private List<String> photoPaths;

        public static AuctionInfo of(Auction auction, List<String> photoPaths) {
            return AuctionInfo.builder()
                    .id(auction.getAuctionUuId())
                    .title(auction.getTitle())
                    .content(auction.getContent())
                    .startPrice(auction.getStartPrice())
                    .currentPrice(auction.getCurrentPrice())
                    .like(auction.getLikes())
                    .createdAt(auction.getCreatedAt())
                    .finishedAt(auction.getCreatedAt().plusDays(DEFAULT_AUCTION_FINISH_DAYS))
                    .photoPaths(photoPaths)
                    .build();
        }
    }

    @Builder @Getter
    @AllArgsConstructor
    private static class BidderInfo {
        private Long id;
        private String name;
        private String email;
        private String photoPath;
        private Long bidPrice;
        private LocalDateTime bidDate;

        public static BidderInfo of(AuctionBidInfo info) {
            return BidderInfo.builder()
                    .id(info.getBidderId())
                    .name(info.getName())
                    .email(info.getUsername())
                    .photoPath(info.getProfilePhotoPath())
                    .bidPrice(info.getBidPrice())
                    .bidDate(info.getBidDate())
                    .build();
        }
    }
}
