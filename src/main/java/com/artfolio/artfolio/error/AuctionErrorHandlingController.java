package com.artfolio.artfolio.error;

import com.artfolio.artfolio.exception.AuctionAlreadyExistsException;
import com.artfolio.artfolio.exception.AuctionAlreadyFinishedException;
import com.artfolio.artfolio.exception.AuctionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static com.artfolio.artfolio.util.ErrorBuildFactory.*;

@Slf4j
@RestControllerAdvice
public class AuctionErrorHandlingController {
    /* validation 검증 실패시 발생하는 예외 핸들링 메서드 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("프론트로부터 잘못된 요청 값이 전달됨");
        List<ErrorResponse.FieldError> fieldErrors = getFieldErrors(e.getBindingResult());
        return buildFieldErrors(ErrorCode.INPUT_VALUE_INVALID, fieldErrors);
    }

    /* 타입이 맞지 않는 값이 넘어온 경우 발생하는 예외 핸들링 메서드 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("프론트로부터 타입이 맞지 않는 요청 값이 전달됨");
        ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError(e.getPropertyName(), (String) e.getValue(), e.getMessage());
        return buildFieldErrors(ErrorCode.INPUT_VALUE_INVALID, List.of(fieldError));
    }

    /* 존재하지 않는 경매 번호가 넘어왔을 때 예외 핸들링 메서드 */
    @ExceptionHandler(AuctionNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleAuctionNotFoundException(AuctionNotFoundException e) {
        log.error("프론트로부터 잘못된 경매 번호가 전달되었습니다. auction ID : " + e.getAuctionId());
        return buildError(ErrorCode.AUCTION_NOT_FOUND);
    }

    /* 이미 낙찰된 경매에 대한 경매 번호가 넘어왔을 때 예외 핸들링 메서드 */
    @ExceptionHandler(AuctionAlreadyFinishedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleAuctionAlreadyFinishedException(AuctionAlreadyFinishedException e) {
        log.error("이미 종료된 경매 건입니다. auction ID : " + e.getAuctionId());
        return buildError(ErrorCode.AUCTION_ALREADY_FINISHED);
    }

    /* 예술품에 대해 진행중인 경매가 이미 있는데 동일한 예술품 ID로 새 경매를 생성하는 경우 예외 처리 */
    @ExceptionHandler(AuctionAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleAuctionAlreadyExistsException(AuctionAlreadyExistsException e) {
        log.error("해당 예술품에 이미 진행중인 경매가 존재합니다. artPiece ID : " + e.getArtPieceId());
        return buildError(ErrorCode.AUCTION_ALREADY_EXISTS);
    }
}
