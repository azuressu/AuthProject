package com.auth.presentation.controller;

import com.auth.application.service.AuthService;
import com.auth.presentation.dto.ApiResponse;
import com.auth.presentation.dto.SignUpRequest;
import com.auth.presentation.dto.SignUpResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     *
     * @param request : 회원가입에 담길 내용
     * @return : 회원가입 성공 결과
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        SignUpResponse response = authService.signUp(request);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

}
