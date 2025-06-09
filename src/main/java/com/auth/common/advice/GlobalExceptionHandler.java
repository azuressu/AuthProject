package com.auth.common.advice;

import com.auth.common.dto.ErrorResponse;
import com.auth.common.exception.CustomException;
import com.auth.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Custom 예외 처리 Handler
     *
     * @param exception : 예외
     * @return : 상태값과 메시지 반환
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 회원가입 시 Valid 관련 예외 처리 Handler
     *
     * @param exception : 예외
     * @return : 상태값과 메시지 반환
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_INPUT_VALUE);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


}
