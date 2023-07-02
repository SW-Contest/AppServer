package com.artfolio.artfolio.global.error;

import com.artfolio.artfolio.global.exception.DeleteAuthorityException;
import com.artfolio.artfolio.global.exception.DuplicateIdException;
import com.artfolio.artfolio.global.exception.UserNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static com.artfolio.artfolio.global.util.ErrorBuildFactory.*;

@Slf4j
@RestControllerAdvice
public class UserErrorHandlingController {
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleEntityNotFoundException() {
        log.error("해당 엔티티를 찾을 수 없습니다.");
        return buildError(ErrorCode.USER_NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleMemberNotFoundException() {
        log.error("프론트로부터 잘못된 멤버 id가 전달되었습니다.");
        return buildError(ErrorCode.USER_NOT_FOUND);
    }

    @ExceptionHandler(DeleteAuthorityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleMemberNotAuthorityException() {
        log.error("해당 예술가가 등록한 경매가 아니여서 경매를 삭제할 수 없습니다.");
        return buildError(ErrorCode.NO_DELETE_AUTHORITY);
    }

    @ExceptionHandler(DuplicateIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleDuplicateIdException () {
        log.error("이미 존재하는 회원입니다.");
        return buildError(ErrorCode.DUPLICATE_ID);
    }

    @ExceptionHandler(WebClientResponseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleWebClientResponseException() {
        log.error("OpenAI 크레딧 부족");
        return buildError(ErrorCode.OPENAI_NOT_AVAILABLE);
    }
}
