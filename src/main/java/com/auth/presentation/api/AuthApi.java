package com.auth.presentation.api;

import com.auth.common.dto.ErrorResponse;
import com.auth.infrastructure.security.UserDetailsImpl;
import com.auth.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "사용자 API", description = "사용자 API 모음")
public interface AuthApi {

    // 회원가입
    @Operation(summary = "회원가입", description = "회원가입 API")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "회원가입 성공",
                            content = @Content(schema = @Schema(implementation = SignUpResponse.class))),
                    @ApiResponse(responseCode = "400", description = "회원가입 실패",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<com.auth.presentation.dto.ApiResponse<SignUpResponse>> signUp(
            @Valid @RequestBody SignUpRequest request);

    @Operation(summary = "관리자 회원가입", description = "관리자 회원가입 API")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "관리자 회원가입 성공",
                            content = @Content(schema = @Schema(implementation = AdminSignUpResponse.class))),
                    @ApiResponse(responseCode = "400", description = "관리자 회원가입 실패",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<com.auth.presentation.dto.ApiResponse<AdminSignUpResponse>> adminSignUp(
            @Valid @RequestBody AdminSignUpRequest request);

    @Operation(summary = "관리자 역할 부여", description = "관리자 역할 부여 API")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "관리자 역할 부여 성공",
                            content = @Content(schema = @Schema(implementation = SwitchRoleResponse.class))),
                    @ApiResponse(responseCode = "400", description = "관리자 역할 부여 실패",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<com.auth.presentation.dto.ApiResponse<SwitchRoleResponse>> switchRole(
            @PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails);

}
