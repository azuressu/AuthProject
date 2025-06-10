package com.auth.presentation.controller;

import com.auth.application.aop.ValidateAdmin;
import com.auth.application.service.AuthService;
import com.auth.infrastructure.security.UserDetailsImpl;
import com.auth.presentation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 관리자 회원가입
     *
     * @param request : 회원가입에 담길 내용
     * @return : 관리자 회원가입 성공 결과
     */
    @PostMapping("/admin/signup")
    public ResponseEntity<ApiResponse<SignUpResponse>> adminSignUp(@Valid @RequestBody AdminSignUpRequest request) {
        SignUpResponse response = authService.adminSignUp(request);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    /**
     * 사용자에게 Admin 권한 부여
     *
     * @param userId      : 권한을 부여할 사용자의 ID
     * @param userDetails : 권한을 부여하는 사용자
     * @return : 권한 부여 여부
     */
    @ValidateAdmin(roles = {"ADMIN"})
    @PatchMapping("/admin/users/{userId}/roles")
    public ResponseEntity<ApiResponse<SwitchRoleResponse>> switchRole(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SwitchRoleResponse response = authService.switchRole(userId, userDetails.getUser());
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

}
