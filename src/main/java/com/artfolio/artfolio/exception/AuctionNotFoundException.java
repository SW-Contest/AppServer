package com.artfolio.artfolio.exception;

import lombok.Getter;

@Getter
public class AuctionNotFoundException extends RuntimeException {
    private final Long auctionId;

    public AuctionNotFoundException(Long auctionId) {
        this.auctionId = auctionId;
    }
}
