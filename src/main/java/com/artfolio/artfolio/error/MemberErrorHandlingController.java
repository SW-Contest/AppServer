package com.artfolio.artfolio.error;

import com.artfolio.artfolio.exception.DeleteAuthorityException;
import com.artfolio.artfolio.exception.MemberNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.artfolio.artfolio.util.ErrorBuildFactory.*;

@Slf4j
@RestControllerAdvice
public class MemberErrorHandlingController {
    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMemberNotFoundException() {
        log.error("프론트로부터 잘못된 멤버 id가 전달되었습니다.");
        return buildError(ErrorCode.MEMBER_NOT_FOUND);
    }

    @ExceptionHandler(DeleteAuthorityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMemberNotAuthorityException() {
        log.error("해당 예술가가 등록한 경매가 아니여서 경매를 삭제할 수 없습니다.");
        return buildError(ErrorCode.NO_DELETE_AUTHORITY);
    }
}