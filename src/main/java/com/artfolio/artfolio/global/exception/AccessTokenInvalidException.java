package com.artfolio.artfolio.global.exception;

public class AccessTokenInvalidException extends RuntimeException {
    private final String accessToken;

    public AccessTokenInvalidException(String accessToken) {
        this.accessToken = accessToken;
    }
}
