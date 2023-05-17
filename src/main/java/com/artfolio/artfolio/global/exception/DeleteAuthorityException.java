package com.artfolio.artfolio.global.exception;

import lombok.Getter;

@Getter
public class DeleteAuthorityException extends RuntimeException {
    private final Long memberId;

    public DeleteAuthorityException(Long memberId) {
        this.memberId = memberId;
    }
}
