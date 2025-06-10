package com.auth.presentation.dto;

import com.auth.domain.entity.User;
import com.auth.domain.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSignUpRequest {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Schema(description = "사용자명(아이디)", example = "admin")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Schema(description = "비밀번호", example = "password")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Schema(description = "닉네임", example = "admin")
    private String nickname;

    @Schema(description = "관리자 키", example = "adminkey")
    private String adminKey;

    public User to(String password) {
        return User.builder()
                .username(this.username)
                .nickname(this.nickname)
                .password(password)
                .userRole(UserRole.ADMIN)
                .build();
    }

}
