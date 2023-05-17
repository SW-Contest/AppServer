package com.artfolio.artfolio.app.dto;

import com.artfolio.artfolio.app.domain.redis.AuctionBidInfo;
import lombok.*;

import java.time.LocalDateTime;

/* 입찰 갱신 DTO */
public class AuctionBid {
    @Getter
    @AllArgsConstructor
    public static class Req {
        private String auctionId;
        private Long bidderId;
        private Long price;
    }

    @Builder @Getter
    @AllArgsConstructor
    public static class Res {
        private BidderInfo bidderInfo;
        private Long bidPrice;
        private LocalDateTime bidDate;

        public static Res of(AuctionBidInfo info) {
            BidderInfo bidderInfo = BidderInfo.builder()
                    .id(info.getBidderId())
                    .username(info.getUsername())
                    .name(info.getName())
                    .email(info.getEmail())
                    .photoPath(info.getProfilePhotoPath())
                    .like(info.getLike())
                    .build();

            return Res.builder()
                    .bidderInfo(bidderInfo)
                    .bidPrice(info.getBidPrice())
                    .bidDate(info.getBidDate())
                    .build();
        }
    }

    @Getter @Builder
    @AllArgsConstructor
    private static class BidderInfo {
        private Long id;
        private String username;
        private String name;
        private String email;
        private String photoPath;
        private Integer like;
    }
}