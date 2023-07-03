package com.artfolio.artfolio.business.dto;

import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.business.domain.redis.RealTimeAuctionInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

public class AuctionPreviewList {
    @Getter
    @AllArgsConstructor
    public static class Res {
        private Boolean hasNext;
        private Boolean isLast;
        private Integer pageSize;
        private Integer pageNumber;
        private Integer dataSize;
        private List<PreviewInfo> data;

        public static Res of(Slice<RealTimeAuctionInfo> infos, List<PreviewInfo> data) {
            int size = data.size();

            return new Res(
                    infos.hasNext(),
                    infos.isLast(),
                    infos.getSize(),
                    infos.getNumber(),
                    size,
                    data
            );
        }
    }

    @Getter
    @AllArgsConstructor
    public static class PreviewInfo {
        private ArtistInfo artistInfo;
        private AuctionInfo auctionInfo;

        public static PreviewInfo of(RealTimeAuctionInfo info, User artist, String thumbnailPath) {
            ArtistInfo artistInfo = ArtistInfo.builder()
                    .id(artist.getId())
                    .email(artist.getEmail())
                    .username(artist.getEmail())
                    .name(artist.getNickname())
                    .like(artist.getLike())
                    .profilePath(artist.getProfilePhoto())
                    .build();

            AuctionInfo auctionInfo = AuctionInfo.builder()
                    .id(info.getId())
                    .like(info.getAuctionLike())
                    .currentPrice(info.getAuctionCurrentPrice())
                    .title(info.getAuctionTitle())
                    .artPieceTitle(info.getArtPieceTitle())
                    .thumbnailPath(thumbnailPath)
                    .build();

            return new PreviewInfo(artistInfo, auctionInfo);
        }
    }

    @Getter @Builder
    @AllArgsConstructor
    private static class AuctionInfo {
        private String id;
        private Integer like;
        private Long currentPrice;
        private String title;
        private String artPieceTitle;
        private String thumbnailPath;
    }

    @Getter @Builder
    @AllArgsConstructor
    private static class ArtistInfo {
        private Long id;
        private String username;
        private String name;
        private String email;
        private String profilePath;
        private Integer like;
    }
}
