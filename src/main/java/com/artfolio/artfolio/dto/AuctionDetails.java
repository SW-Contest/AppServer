package com.artfolio.artfolio.dto;

import com.artfolio.artfolio.domain.ArtPiecePhoto;
import com.artfolio.artfolio.domain.Member;
import com.artfolio.artfolio.domain.redis.AuctionBidInfo;
import com.artfolio.artfolio.domain.redis.RealTimeAuctionInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class AuctionDetails {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Res {
        private ArtistInfo artistInfo;
        private AuctionInfo auctionInfo;
        private List<String> photoPaths;
        private List<BidderInfo> bidderInfos;

        public static Res of(RealTimeAuctionInfo realTimeAuctionInfo, List<AuctionBidInfo> bidInfo, List<ArtPiecePhoto> paths, Member artist) {
            ArtistInfo artistInfo = ArtistInfo.builder()
                    .id(artist.getId())
                    .like(artist.getLike())
                    .name(artist.getName())
                    .email(artist.getEmail())
                    .photoPath(artist.getProfilePhoto())
                    .build();

            AuctionInfo auctionInfo = AuctionInfo.builder()
                    .id(realTimeAuctionInfo.getId())
                    .title(realTimeAuctionInfo.getAuctionTitle())
                    .content(realTimeAuctionInfo.getAuctionContent())
                    .startPrice(realTimeAuctionInfo.getAuctionStartPrice())
                    .currentPrice(realTimeAuctionInfo.getAuctionCurrentPrice())
                    .like(realTimeAuctionInfo.getAuctionLike())
                    .createdAt(realTimeAuctionInfo.getCreatedAt())
                    .build();

            List<BidderInfo> bidderInfos = bidInfo.stream()
                    .map(BidderInfo::of)
                    .sorted((o1, o2) -> o2.getBidDate().compareTo(o1.getBidDate()))
                    .toList();

            List<String> photoPaths = paths.stream()
                    .map(ArtPiecePhoto::getFilePath)
                    .toList();

            return Res.builder()
                    .artistInfo(artistInfo)
                    .auctionInfo(auctionInfo)
                    .photoPaths(photoPaths)
                    .bidderInfos(bidderInfos)
                    .build();
        }
    }

    @Builder @Getter
    @AllArgsConstructor
    private static class ArtistInfo {
        private Long id;
        private Long like;
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
        private Long like;
        private LocalDateTime createdAt;
    }

    @Builder @Getter
    @AllArgsConstructor
    private static class BidderInfo {
        private Long id;
        private String name;
        private String email;
        private String photoPath;
        private Long like;
        private Long bidPrice;
        private LocalDateTime bidDate;

        public static BidderInfo of(AuctionBidInfo info) {
            return BidderInfo.builder()
                    .id(info.getBidderId())
                    .name(info.getName())
                    .email(info.getEmail())
                    .photoPath(info.getProfilePhotoPath())
                    .like(info.getLike())
                    .bidPrice(info.getBidPrice())
                    .bidDate(info.getBidDate())
                    .build();
        }
    }
}
