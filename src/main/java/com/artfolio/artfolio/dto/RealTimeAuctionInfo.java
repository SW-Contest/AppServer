package com.artfolio.artfolio.dto;

/* 실시간 경매 정보 DTO */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RedisHash(value = "auction", timeToLive = 3600 * 24 * 7)   // 만료 1주일
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class RealTimeAuctionInfo { // Data transfer Object
    @Id @JsonProperty("auctionId")
    private String id;
    private Long artistId;                           // 작가 ID
    @Indexed private Long artPieceId;                         // 예술품 ID
    private String artPieceTitle;                    // 예술품 제목
    private String auctionTitle;                     // 경매 제목
    private String auctionContent;                   // 경매 설명글
    private Long auctionStartPrice;                  // 경매 시작가
    private Long auctionCurrentPrice;                // 경매 현재가
    @JsonIgnore private Long bidderId;               // 낙찰자 ID (nullable)
    private Long like;                               // 경매 좋아요 개수
    private LocalDateTime createdAt;                 // 경매 생성일시
    private Set<Long> likeMembers = new HashSet<>();       // 좋아요 누른 멤버 ID 목록
    private List<String> photoPaths = new ArrayList<>();   // 사진 경로

    public void updateAuctionPhoto(String photoPath) {
        this.photoPaths.add(photoPath);
    }
}
