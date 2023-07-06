package com.artfolio.artfolio.business.dto;

import com.artfolio.artfolio.business.domain.ArtPiece;
import com.artfolio.artfolio.business.domain.ArtPiecePhoto;
import com.artfolio.artfolio.business.domain.Auction;
import com.artfolio.artfolio.business.domain.redis.AuctionBidInfo;
import com.artfolio.artfolio.user.entity.User;
import lombok.*;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AuctionDto {
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
            ArtistInfo artistInfo = ArtistInfo.builder()
                    .id(artist.getId())
                    .email(artist.getEmail())
                    .username(artist.getEmail())
                    .name(artist.getNickname())
                    .photoPath(artist.getProfilePhoto())
                    .build();

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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailInfoRes {
        private ArtistInfo artistInfo;
        private ArtPieceInfo artPieceInfo;
        private AuctionInfo auctionInfo;
        private List<BidderInfo> bidderInfos;

        public static DetailInfoRes of(Auction auction, List<AuctionBidInfo> bidInfo, List<ArtPiecePhoto> paths, User artist, ArtPiece artPiece) {
            ArtistInfo artistInfo = ArtistInfo.builder()
                    .id(artist.getId())
                    .username(artist.getEmail())
                    .name(artist.getNickname())
                    .email(artist.getEmail())
                    .photoPath(artist.getProfilePhoto())
                    .build();

            List<String> photoPaths = paths.stream()
                    .map(ArtPiecePhoto::getFilePath)
                    .toList();

            AuctionInfo auctionInfo = AuctionInfo.builder()
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

            ArtPieceInfo artPieceInfo = ArtPieceInfo.builder()
                    .id(artPiece.getId())
                    .title(artPiece.getTitle())
                    .content(artPiece.getContent())
                    .like(artPiece.getLikes())
                    .build();

            List<BidderInfo> bidderInfos = bidInfo.stream()
                    .map(BidderInfo::of)
                    .sorted(Comparator.comparing(BidderInfo::getBidDate))
                    .toList();

            return DetailInfoRes.builder()
                    .artistInfo(artistInfo)
                    .artPieceInfo(artPieceInfo)
                    .auctionInfo(auctionInfo)
                    .bidderInfos(bidderInfos)
                    .build();
        }
    }

    @Builder @Getter
    @AllArgsConstructor
    private static class ArtPieceInfo {
        private Long id;
        private String title;
        private String content;
        private Integer like;
    }

    @Builder @Getter
    @AllArgsConstructor
    private static class ArtistInfo {
        private Long id;
        private String username;
        private String name;
        private String email;
        private String photoPath;
    }

    @Builder @Getter
    @AllArgsConstructor
    private static class AuctionInfo {
        private String id;
        private String title;
        private String content;
        private Long startPrice;
        private Long currentPrice;
        private Integer like;
        private LocalDateTime createdAt;
        private LocalDateTime finishedAt;
        private List<String> photoPaths;
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
