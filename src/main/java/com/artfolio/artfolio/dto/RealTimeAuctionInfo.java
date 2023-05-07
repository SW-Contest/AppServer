package com.artfolio.artfolio.dto;

/* 실시간 경매 정보 DTO */

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @JsonIgnore private Long bidderId;         // 낙찰자 ID (nullable)
    private Long like;                     // 경매 좋아요 개수
    private Set<Long> likeMembers = new HashSet<>();   // 좋아요 누른 멤버 ID 목록
    private List<String> photoPaths = new ArrayList<>();   // 사진 경로
}
