package com.artfolio.artfolio.global.error;

import com.artfolio.artfolio.global.exception.ArtPieceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.artfolio.artfolio.global.util.ErrorBuildFactory.*;

@Slf4j
@RestControllerAdvice
public class ArtPieceErrorHandlingController {
    /* 존재하지 않는 예술품 번호가 넘어온 경우 */
    @ExceptionHandler(ArtPieceNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleArtPieceNotFoundException(ArtPieceNotFoundException e) {
        log.error("해당 예술품 번호가 존재하지 않습니다.");
        log.error("art piece ID : " + e.getArtPieceId());
        return buildError(ErrorCode.ARTPIECE_NOT_FOUND);
    }
}
