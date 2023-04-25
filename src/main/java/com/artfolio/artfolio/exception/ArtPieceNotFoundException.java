package com.artfolio.artfolio.exception;

import lombok.Getter;

@Getter
public class ArtPieceNotFoundException extends RuntimeException {
    private final Long artPieceId;

    public ArtPieceNotFoundException(Long artPieceId) {
        this.artPieceId = artPieceId;
    }
}
