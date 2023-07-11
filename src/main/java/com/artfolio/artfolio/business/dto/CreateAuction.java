package com.artfolio.artfolio.business.dto;

/* 실시간 경매 생성 요청 DTO */

import lombok.Builder;
import lombok.Getter;

public class CreateAuction {
    @Getter
    public static class Req {
        private final Long artistId;
        private final Long artPieceId;
        private final String auctionTitle;
        private final String auctionContent;
        private final Long auctionStartPrice;

        public Req(Long artistId, Long artPieceId, String auctionTitle, String auctionContent, Long auctionStartPrice) {
            this.artistId = artistId;
            this.artPieceId = artPieceId;
            this.auctionTitle = auctionTitle;
            this.auctionContent = auctionContent;
            this.auctionStartPrice = auctionStartPrice;
        }
    }

    @Getter
    public static class Res {
        private final String auctionId;

        @Builder
        private Res(String auctionId) {
            this.auctionId = auctionId;
        }

        public static Res of(String auctionId) {
            return Res.builder()
                    .auctionId(auctionId)
                    .build();
        }
    }
}
