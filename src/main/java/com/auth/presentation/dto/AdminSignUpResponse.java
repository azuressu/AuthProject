package com.auth.presentation.dto;

import com.auth.domain.entity.User;
import com.auth.domain.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSignUpResponse {

    @Schema(description = "사용자명(아이디)", example = "username")
    private String username;

    @Schema(description = "닉네임", example = "nickname")
    private String nickname;

    private List<RoleDto> roles;

    public static AdminSignUpResponse from(User user) {
        return AdminSignUpResponse.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .roles(List.of(new AdminSignUpResponse.RoleDto(user.getUserRole())))
                .build();
    }

    @Getter
    public static class RoleDto {

        @Schema(description = "역할 이름", example = "ADMIN")
        private String role;

        public RoleDto(UserRole role) {
            this.role = role.toString();
        }

    }

}
