package com.artfolio.artfolio.exception;

import lombok.Getter;

@Getter
public class AuctionAlreadySoldException extends RuntimeException {
    private final Long auctionId;

    public AuctionAlreadySoldException(Long auctionId) {
        this.auctionId = auctionId;
    }
}
