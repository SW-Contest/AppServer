package com.artfolio.artfolio.exception;

import lombok.Getter;

@Getter
public class AuctionAlreadyExistsException extends RuntimeException {
    private final Long artPieceId;

    public AuctionAlreadyExistsException(Long artPieceId) {
        this.artPieceId = artPieceId;
    }
}
