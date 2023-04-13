package com.artfolio.artfolio.error;

import com.artfolio.artfolio.exception.AuctionAlreadySoldException;
import com.artfolio.artfolio.exception.AuctionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlingController {
    /* validation 검증 실패시 발생하는 예외 핸들링 메서드 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("프론트로부터 잘못된 요청 값이 전달됨");
        List<ErrorResponse.FieldError> fieldErrors = getFieldErrors(e.getBindingResult());
        return buildFieldErrors(ErrorCode.INPUT_VALUE_INVALID, fieldErrors);
    }

    /* 존재하지 않는 경매 번호가 넘어왔을 때 예외 핸들링 메서드 */
    @ExceptionHandler(AuctionNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleAuctionNotFoundException(AuctionNotFoundException e) {
        log.error("프론트로부터 잘못된 경매 번호가 전달되었습니다. auction ID : " + e.getAuctionId());
        return buildError(ErrorCode.AUCTION_NOT_FOUND);
    }

    /* 이미 낙찰된 경매에 대한 경매 번호가 넘어왔을 때 예외 핸들링 메서드 */
    @ExceptionHandler(AuctionAlreadySoldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleAuctionAlreadySoldException(AuctionAlreadySoldException e) {
        log.error("이미 종료된 경매 건입니다. auction ID : " + e.getAuctionId());
        return buildError(ErrorCode.AUCTION_ALREADY_SOLD);
    }

    private List<ErrorResponse.FieldError> getFieldErrors(BindingResult binding) {
        return binding.getFieldErrors()
                .parallelStream()
                .map(err -> new ErrorResponse.FieldError(
                        err.getField(),
                        (String) err.getRejectedValue(),
                        err.getDefaultMessage()
                ))
                .collect(Collectors.toList());
    }

    private ErrorResponse buildFieldErrors(ErrorCode errorCode, List<ErrorResponse.FieldError> fieldErrors) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getCode(),
                errorCode.getStatus(),
                fieldErrors
        );
    }

    private ErrorResponse buildError(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                errorCode.getStatus(),
                List.of()
        );
    }
}
