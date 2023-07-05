package com.artfolio.artfolio.global.exception;

import lombok.Getter;

@Getter
public class AccessTokenInvalidException extends RuntimeException {
    private final String accessToken;

    public AccessTokenInvalidException(String accessToken) {
        this.accessToken = accessToken;
    }
}
