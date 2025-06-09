package com.auth.presentation.dto;

import com.auth.domain.entity.User;
import com.auth.domain.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponse {

    private String username;
    private String nickname;
    private List<RoleDto> roles;

    public static SignUpResponse from(User user) {
        return SignUpResponse.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .roles(List.of(new RoleDto(user.getUserRole())))
                .build();
    }

    @Getter
    public static class RoleDto {
        private String role;

        public RoleDto(UserRole role) {
            this.role = role.toString();
        }

    }

}
