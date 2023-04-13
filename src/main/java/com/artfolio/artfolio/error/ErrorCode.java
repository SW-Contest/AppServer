package com.artfolio.artfolio.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INPUT_VALUE_INVALID("INPUT_VALUE_INVALID", "데이터 형식이 맞지 않습니다.", 400),
    AUCTION_NOT_FOUND("AUCTION_NOT_FOUND", "해당 경매를 찾을 수 없습니다.", 400),
    AUCTION_ALREADY_FINISHED("AUCTION_ALREADY_FINISHED", "이미 종료된 경매 건입니다.", 400)
    ;

    private final String code;
    private final String message;
    private final Integer status;

    ErrorCode(String code, String message, Integer status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
