package com.artfolio.artfolio.global.exception;

import lombok.Getter;

@Getter
public class ArtPieceNotFoundException extends RuntimeException {
    private final Long artPieceId;

    public ArtPieceNotFoundException(Long artPieceId) {
        this.artPieceId = artPieceId;
    }
}
