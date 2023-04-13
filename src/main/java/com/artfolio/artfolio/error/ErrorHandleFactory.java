package com.artfolio.artfolio.error;

import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

public class ErrorHandleFactory {
    public static List<ErrorResponse.FieldError> getFieldErrors(BindingResult binding) {
        return binding.getFieldErrors()
                .stream()
                .map(err -> new ErrorResponse.FieldError(
                        err.getField(),
                        (String) err.getRejectedValue(),
                        err.getDefaultMessage()
                ))
                .collect(Collectors.toList());
    }

    public static ErrorResponse buildFieldErrors(ErrorCode errorCode, List<ErrorResponse.FieldError> fieldErrors) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getCode(),
                errorCode.getStatus(),
                fieldErrors
        );
    }

    public static ErrorResponse buildError(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                errorCode.getStatus(),
                List.of()
        );
    }
}
