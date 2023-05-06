package com.artfolio.artfolio.dto;

/* 실시간 경매 생성 요청 DTO */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class RealTimeAuctionInfo {
    private Long artistId;                 // 작가 ID
    private Long artPieceId;               // 예술품 ID
    private String artPieceName;           // 예술품 이름
    private String auctionTitle;           // 경매 제목
    private String auctionContent;         // 경매 설명글
    private Long auctionStartPrice;        // 경매 시작가
    private Long auctionCurrentPrice;      // 경매 현재가
    private Long like;                     // 경매 좋아요 개수
    private Long bidderId;                 // 낙찰자 ID (nullable)
}
