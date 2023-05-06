package com.artfolio.artfolio.dto;

import com.artfolio.artfolio.domain.ArtPiece;
import com.artfolio.artfolio.domain.ArtPiecePhoto;
import com.artfolio.artfolio.domain.Auction;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;
import java.util.List;


@JsonPropertyOrder({
        "creatorName",        /* 작가 이름 */
        "artPieceTitle",      /* 작품 제목 */
        "artPieceContent",    /* 작품 소개글 */
        "artPieceLike",       /* 작품 좋아요 개수 */
        "auctionStartPrice",  /* 경매 시작가 */
        "auctionStartTime",   /* 경매 시작 시간 */
        "photoLength",        /* 작품 사진 목록 개수 */
        "photoPaths"          /* 작품 사진 경로 목록 */
})
public record DetailPageInfoRes(
    List<String> photoPaths,
    Integer photoLength,
    String artistName,
    String artPieceTitle,
    String artPieceContent,
    Long artPieceLike,
    Long auctionStartPrice,
    LocalDateTime auctionStartTime
) {
    public static DetailPageInfoRes of(Auction auction) {
        ArtPiece piece = auction.getArtPiece();

        return new DetailPageInfoRes(
                piece.getArtPiecePhotos()
                        .stream()
                        .map(ArtPiecePhoto::getFilePath)
                        .toList(),
                piece.getArtPiecePhotos().size(),
                piece.getArtist().getName(),
                piece.getTitle(),
                piece.getContent(),
                piece.getLike(),
                auction.getStartPrice(),
                auction.getCreatedAt()
        );
    }
}

