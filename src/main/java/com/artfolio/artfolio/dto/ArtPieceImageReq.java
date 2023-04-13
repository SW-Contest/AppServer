package com.artfolio.artfolio.dto;

import com.artfolio.artfolio.domain.ArtPiece;
import com.artfolio.artfolio.domain.ArtPiecePhoto;
import lombok.Builder;

public record ArtPieceImageReq(
        String originalFileName,
        String hashedFileName,
        String contentType,
        String extension,
        Long size
) {
    @Builder
    public ArtPieceImageReq { }

    public ArtPiecePhoto toEntity(ArtPiece artPiece, String path, Boolean isThumbnail) {
        return ArtPiecePhoto.builder()
                .artPiece(artPiece)
                .originalFilename(originalFileName)
                .hashedFilename(hashedFileName)
                .fileExtension(extension)
                .contentType(contentType)
                .filePath(path)
                .isThumbnail(isThumbnail)
                .size(size)
                .build();
    }
}
