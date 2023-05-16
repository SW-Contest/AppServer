package com.artfolio.artfolio.global.exception;

import lombok.Getter;

import java.security.Principal;

@Getter
public class InvalidBidPriceException extends RuntimeException {
    private final Principal principal;
    private final Long currentPrice;
    private final Long bidPrice;
    private final String auctionKey;

    public InvalidBidPriceException(Principal principal, Long currentPrice, Long bidPrice, String auctionKey) {
        this.principal = principal;
        this.currentPrice = currentPrice;
        this.bidPrice = bidPrice;
        this.auctionKey = auctionKey;
    }
}
