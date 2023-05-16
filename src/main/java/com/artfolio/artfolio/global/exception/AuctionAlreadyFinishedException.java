package com.artfolio.artfolio.global.exception;

import lombok.Getter;

@Getter
public class AuctionAlreadyFinishedException extends RuntimeException {
    private final Long auctionId;

    public AuctionAlreadyFinishedException(Long auctionId) {
        this.auctionId = auctionId;
    }
}
