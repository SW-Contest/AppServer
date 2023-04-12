package com.artfolio.artfolio.dto.auction;

import com.artfolio.artfolio.domain.ArtPiece;
import com.artfolio.artfolio.domain.ArtPiecePhoto;
import com.artfolio.artfolio.domain.Auction;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@JsonPropertyOrder({
        "creatorName",        /* 작가 이름 */
        "artPieceTitle",      /* 작품 제목 */
        "artPieceContent",    /* 작품 소개글 */
        "artPieceLike",       /* 작품 좋아요 개수 */
        "auctionStartPrice",  /* 경매 시작가 */
        "auctionStartTime",   /* 경매 시작 시간 */
        "artPiecePhotoPaths", /* 작품 사진 경로 목록 */
        "photoLength"
})
public class DetailPageInfoRes {
    @JsonProperty("photoPaths")
    private Set<String> paths;
    private Integer photoLength;
    private String creatorName;
    private String artPieceTitle;
    private String artPieceContent;
    private Long artPieceLike;
    private Long auctionStartPrice;
    private LocalDateTime auctionStartTime;

    @Builder
    private DetailPageInfoRes(
            Set<String> paths,
            String creatorName,
            String artPieceTitle,
            String artPieceContent,
            Long artPieceLike,
            Long auctionStartPrice,
            Integer photoLength,
            LocalDateTime auctionStartTime
    ) {
        this.paths = paths;
        this.creatorName = creatorName;
        this.artPieceTitle = artPieceTitle;
        this.artPieceContent = artPieceContent;
        this.artPieceLike = artPieceLike;
        this.auctionStartPrice = auctionStartPrice;
        this.photoLength = photoLength;
        this.auctionStartTime = auctionStartTime;
    }

    public static DetailPageInfoRes of(Auction auction) {
        ArtPiece piece = auction.getArtPiece();

        return DetailPageInfoRes.builder()
                .paths(piece.getArtPiecePhotos()
                        .stream()
                        .map(ArtPiecePhoto::getFilePath)
                        .collect(Collectors.toSet())
                )
                .photoLength(piece.getArtPiecePhotos().size())
                .creatorName(piece.getCreator().getName())
                .artPieceTitle(piece.getTitle())
                .artPieceContent(piece.getContent())
                .artPieceLike(piece.getLike())
                .auctionStartPrice(auction.getStartPrice())
                .auctionStartTime(auction.getCreatedAt())
                .build();
    }
}
