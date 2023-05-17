package com.artfolio.artfolio.global.exception;

import lombok.Getter;

@Getter
public class DuplicateIdException extends RuntimeException {
    private final String username;
    public DuplicateIdException(String username) {
        this.username = username;
    }
}
