package com.artfolio.artfolio.global.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final Object userId;

    public UserNotFoundException(Object userId) {
        this.userId = userId;
    }
}
