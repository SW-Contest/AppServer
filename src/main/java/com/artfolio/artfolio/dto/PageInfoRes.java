package com.artfolio.artfolio.dto;

import com.artfolio.artfolio.domain.ArtPiece;
import com.artfolio.artfolio.domain.Auction;
import com.artfolio.artfolio.domain.Member;

import java.time.LocalDateTime;

public record PageInfoRes(
        Long auctionId,
        String thumbnail,
        String artistName,
        String artPieceName,
        LocalDateTime startTime,
        Long like
) {
    public static PageInfoRes of(Auction auction) {
        ArtPiece piece = auction.getArtPiece();
        Member artist = piece.getArtist();
        String path = piece.getArtPiecePhotos().isEmpty() ? "사진 없음" : piece.getArtPiecePhotos().get(0).getFilePath();

        return new PageInfoRes(
                auction.getId(),
                path,
                artist.getName(),
                piece.getTitle(),
                piece.getCreatedAt(),
                piece.getLike()
        );
    }
}
