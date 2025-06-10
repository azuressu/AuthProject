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
public class SwitchRoleResponse {

    private String username;
    private String nickname;
    private List<com.auth.presentation.dto.SignUpResponse.RoleDto> roles;

    public static SwitchRoleResponse from(User user) {
        return SwitchRoleResponse.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .roles(List.of(new com.auth.presentation.dto.SignUpResponse.RoleDto(user.getUserRole())))
                .build();
    }

}
