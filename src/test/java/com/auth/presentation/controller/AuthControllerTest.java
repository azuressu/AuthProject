package com.auth.presentation.controller;

import com.auth.domain.entity.UserRole;
import com.auth.presentation.dto.AdminSignUpRequest;
import com.auth.presentation.dto.SignUpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${admin.key}")
    private String adminKey;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signup_success() throws Exception {
        // given
        String username = "username";
        String nickname = "nickname";

        SignUpRequest request = SignUpRequest.builder()
                .username(username)
                .password("secure123!")
                .nickname(nickname)
                .build();

        // when & then
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value(username))
                .andExpect(jsonPath("$.data.nickname").value(nickname))
                .andDo(print());
    }


    @Test
    @DisplayName("관리자 회원가입 성공 테스트")
    void signup_success_admin() throws Exception {
        // given
        String username = "admin";
        String nickname = "admin";

        AdminSignUpRequest request = AdminSignUpRequest.builder()
                .username(username)
                .password("secure123!")
                .nickname(nickname)
                .adminKey(adminKey)
                .build();

        // when & then
        mockMvc.perform(post("/admin/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value(username))
                .andExpect(jsonPath("$.data.nickname").value(nickname))
                .andExpect(jsonPath("$.data.roles[0].role").value(UserRole.ADMIN.toString()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 테스트_사용자명 중복")
    void signup_error_duplicate_username() throws Exception {
        // given
        String username = "username";

        SignUpRequest request = SignUpRequest.builder()
                .username(username)
                .password("secure123!")
                .nickname("nickname")
                .build();

        // when
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // then
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("USER_ALREADY_EXISTS"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 테스트_잘못된 입력")
    void signup_error_empty_field() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .username("")
                .password("")
                .nickname("")
                .build();

        // when & then
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("관리자 회원가입 실패 테스트_관리자 키 오류")
    void signup_error_admin_adminKey() throws Exception {
        // given
        String username = "admin";
        String nickname = "admin";

        AdminSignUpRequest request = AdminSignUpRequest.builder()
                .username(username)
                .password("secure123!")
                .nickname(nickname)
                .adminKey("admin")
                .build();

        // when & then
        mockMvc.perform(post("/admin/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }


}