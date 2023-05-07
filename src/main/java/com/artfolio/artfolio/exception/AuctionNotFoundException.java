package com.artfolio.artfolio.exception;

import lombok.Getter;

@Getter
public class AuctionNotFoundException extends RuntimeException {
    private final Object auctionId;

    public AuctionNotFoundException(Object auctionId) {
        this.auctionId = auctionId;
    }
}
