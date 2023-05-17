package com.artfolio.artfolio.global.exception;

import lombok.Getter;

@Getter
public class MemberNotFoundException extends RuntimeException {
    private final Object memberId;

    public MemberNotFoundException(Object memberId) {
        this.memberId = memberId;
    }
}
