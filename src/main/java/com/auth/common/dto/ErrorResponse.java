package com.auth.common.dto;

import com.auth.common.exception.ErrorCode;
import lombok.Getter;

public class ErrorResponse {

    private final ErrorDetail error;

    public ErrorResponse(ErrorCode errorCode) {
        this.error = new ErrorDetail(errorCode.getCode(), errorCode.getMessage());
    }

    public ErrorDetail getError() {
        return error;
    }

    @Getter
    public static class ErrorDetail {
        private final String code;
        private final String message;

        public ErrorDetail(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }

}
