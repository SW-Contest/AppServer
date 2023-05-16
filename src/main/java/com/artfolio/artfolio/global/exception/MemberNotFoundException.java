package com.artfolio.artfolio.global.exception;

import lombok.Getter;

@Getter
public class MemberNotFoundException extends RuntimeException {
    private final Long memberId;

    public MemberNotFoundException(Long memberId) {
        this.memberId = memberId;
    }
}
