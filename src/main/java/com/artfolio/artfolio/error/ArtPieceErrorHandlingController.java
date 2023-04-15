package com.artfolio.artfolio.error;

import com.artfolio.artfolio.exception.ArtPieceNotFoundException;
import com.artfolio.artfolio.util.ErrorBuildFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.artfolio.artfolio.util.ErrorBuildFactory.*;

@Slf4j
@RestControllerAdvice
public class ArtPieceErrorHandlingController {
    /* 존재하지 않는 예술품 번호가 넘어온 경우 */
    @ExceptionHandler(ArtPieceNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleArtPieceNotFoundException(ArtPieceNotFoundException e) {
        log.error("프론트로부터 잘못된 예술품 번호가 넘어옴. artPieceId : " + e.getArtPieceId());
        return buildError(ErrorCode.ARTPIECE_NOT_FOUND);
    }
}
