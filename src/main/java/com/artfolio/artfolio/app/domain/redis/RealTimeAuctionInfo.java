package com.artfolio.artfolio.app.domain.redis;


import com.artfolio.artfolio.app.dto.CreateAuction;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* 실시간 경매 정보 */
@RedisHash(value = "auction", timeToLive = 3600 * 24 * 7)   // 만료 1주일
@Getter
public class RealTimeAuctionInfo {
    @Id @JsonProperty("auctionId") private String id;
    @Indexed private Long artPieceId;                               // 예술품 ID
    private Long artistId;
    private String artPieceTitle;                                   // 예술품 제목
    private String auctionTitle;                                    // 경매 제목
    private String auctionContent;                                  // 경매 설명글
    private Long auctionStartPrice;                                 // 경매 시작가
    private Long auctionCurrentPrice;                               // 경매 현재가
    private LocalDateTime createdAt;                                // 경매 생성일시
    private List<String> photoPaths;                                // 사진 경로
    private Integer like;
    private Set<Long> likeMembers;                                 // 좋아요 누른 멤버 목록

    private RealTimeAuctionInfo(
            Long artistId,
            Long artPieceId,
            String artPieceTitle,
            String auctionTitle,
            String auctionContent,
            Long auctionStartPrice
    ) {
        this.artistId = artistId;
        this.artPieceId = artPieceId;
        this.artPieceTitle = artPieceTitle;
        this.auctionTitle = auctionTitle;
        this.auctionContent = auctionContent;
        this.auctionStartPrice = auctionStartPrice;
        this.auctionCurrentPrice = auctionStartPrice;
        this.createdAt = LocalDateTime.now();
        this.photoPaths = new ArrayList<>();
        this.like = 0;
        this.likeMembers = new HashSet<>();
    }

    public static RealTimeAuctionInfo of(CreateAuction.Req req, List<String> photoPaths) {
        RealTimeAuctionInfo realTimeAuctionInfo = new RealTimeAuctionInfo(
                req.getArtistId(),
                req.getArtPieceId(),
                req.getArtPieceTitle(),
                req.getAuctionTitle(),
                req.getAuctionContent(),
                req.getAuctionStartPrice()
        );

        realTimeAuctionInfo.getPhotoPaths().addAll(photoPaths);
        return realTimeAuctionInfo;
    }

    public void updateCurrentPrice(Long curPrice) {
        this.auctionCurrentPrice = curPrice;
    }

    public void addLikeMembers(Long memberId) {
        this.likeMembers.add(memberId);
    }

    public Integer getAuctionLike() {
        return this.likeMembers.size();
    }

    public void updateLike(Integer like) {
        this.like = like;
    }
}