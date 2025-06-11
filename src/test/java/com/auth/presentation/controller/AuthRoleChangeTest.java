package com.auth.presentation.controller;

import com.auth.infrastructure.repository.UserRepository;
import com.auth.presentation.dto.AdminSignUpRequest;
import com.auth.presentation.dto.LoginRequest;
import com.auth.presentation.dto.SignUpRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthRoleChangeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Value("${admin.key}")
    private String adminKey;

    private String username = "username";
    private String nickname = "nickname";
    private String adminUsername = "admin";
    private String adminNickname = "admin1";

    @BeforeEach
    void signup_success_user_admin() throws Exception {
        SignUpRequest request = SignUpRequest.builder()
                .username(username)
                .password("password")
                .nickname(nickname)
                .build();

        AdminSignUpRequest adminRequest = AdminSignUpRequest.builder()
                .username(adminUsername)
                .password("password")
                .nickname(adminNickname)
                .adminKey(adminKey)
                .build();

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(post("/admin/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRequest)));
    }

    @Test
    @DisplayName("권한 변경 성공 테스트")
    void role_change_success() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest(adminUsername, "password");
        MvcResult loginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // when
        String responseBody = loginResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String token = jsonNode.get("token").asText();

        Long targetUserId = userRepository.findByUsername(username)
                .orElseThrow()
                .getUserId();

        // then
        mockMvc.perform(patch("/admin/users/"+targetUserId+"/roles")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("권한 변경 실패 테스트_사용자 존재 X")
    void role_change_error_nouser() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest(adminUsername, "password");
        MvcResult loginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // when
        String responseBody = loginResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String token = jsonNode.get("token").asText();

        // then
        mockMvc.perform(patch("/admin/users/1000/roles")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("USER_NOT_FOUND"))
                .andDo(print());

    }

    @Test
    @DisplayName("권한 변경 실패 테스트_관리자 권한 X")
    void role_change_error_notadmin() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest(username, "password");
        MvcResult loginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // when
        String responseBody = loginResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String token = jsonNode.get("token").asText();

        // then
        mockMvc.perform(patch("/admin/users/2/roles")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                 .andExpect(jsonPath("$.error.code").value("ACCESS_DENIED"))
                .andDo(print());

    }

}
