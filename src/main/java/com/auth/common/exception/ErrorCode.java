package com.auth.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "이미 가입된 사용자입니다."),
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "아이디 또는 비밀번호가 올바르지 않습니다."),
    ACCESS_DENIED("ACCESS_DENIED", "관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다."),
    INVALID_INPUT_VALUE("INVALID_INPUT_VALUE", "입력이 올바르지 않습니다."),
    INVALID_ADMIN_KEY("INVALID_ADMIN_KEY", "관리자 암호가 올바르지 않습니다."),
    INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 인증 토큰입니다.")
    ;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
