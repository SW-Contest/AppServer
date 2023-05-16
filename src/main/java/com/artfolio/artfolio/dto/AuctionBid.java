package com.artfolio.artfolio.dto;

import com.artfolio.artfolio.domain.redis.AuctionBidInfo;
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
        private Long id;
        private String name;
        private String email;
        private String photoPath;
        private Long like;
        private Long price;
        private LocalDateTime bidDate;

        public static Res of(AuctionBidInfo info) {
            return Res.builder()
                    .id(info.getBidderId())
                    .name(info.getName())
                    .email(info.getEmail())
                    .photoPath(info.getProfilePhotoPath())
                    .like(info.getLike())
                    .price(info.getBidPrice())
                    .bidDate(info.getBidDate())
                    .build();
        }
    }
}

