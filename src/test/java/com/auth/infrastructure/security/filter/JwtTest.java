package com.auth.infrastructure.security.filter;

import com.auth.common.exception.ErrorCode;
import com.auth.domain.entity.UserRole;
import com.auth.infrastructure.security.JwtUtil;
import com.auth.infrastructure.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private String validUserToken;
    private String validAdminToken;
    private String expiredToken;
    private String invalidSignatureToken;

    @BeforeEach
    void setUp() throws InterruptedException {
        // 실제 DB 연동 없이 JWT 검증을 위한 모의 UserDetails 객체 생성
        // 일반 사용자
        UserDetails userUser = User.withUsername("testuser").password("password").roles("USER").build();
        Mockito.when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userUser);

        // 관리자
        UserDetails adminUser = User.withUsername("testadmin").password("password").roles("ADMIN").build();
        Mockito.when(userDetailsService.loadUserByUsername("testadmin")).thenReturn(adminUser);

        // 만료된 토큰 사용자
        UserDetails expiredMockUser = User.withUsername("expireduser").password("password").roles("USER").build();
        Mockito.when(userDetailsService.loadUserByUsername("expireduser")).thenReturn(expiredMockUser);

        // 잘못된 토큰 형식의 사용자
        UserDetails tamperedMockUser = User.withUsername("tamperuser").password("password").roles("USER").build();
        Mockito.when(userDetailsService.loadUserByUsername("tamperuser")).thenReturn(tamperedMockUser);

        // 유효한 토큰 생성
        jwtUtil.init();
        validUserToken = jwtUtil.createToken("testuser", UserRole.USER);
        validAdminToken = jwtUtil.createToken("testadmin", UserRole.ADMIN);

        // 잘못된 형식의 토큰 생성
        String baseValidToken = jwtUtil.createToken("tamperuser", UserRole.USER);
        invalidSignatureToken = baseValidToken.substring(0, baseValidToken.length() - 5) + "INVALID_SIG";

        // 만료된 토큰 생성을 위한 임시 설정 및 토큰 생성
        long originalTokenTime = (long) ReflectionTestUtils.getField(jwtUtil, "TOKEN_TIME");
        ReflectionTestUtils.setField(jwtUtil, "TOKEN_TIME", 10L); // 10ms
        jwtUtil.init();
        expiredToken = jwtUtil.createToken("expireduser", UserRole.USER);
        Thread.sleep(50); // 토큰이 확실히 만료되도록 대기
        ReflectionTestUtils.setField(jwtUtil, "TOKEN_TIME", originalTokenTime); // 원래대로 복구
    }

    @Test
    @DisplayName("사용자 - 일반 API 접근 성공")
    void user_access_success() throws Exception {
        mockMvc.perform(get("/api/test")
                        .header(JwtUtil.AUTHORIZATION_HEADER, validUserToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("USER"));
    }

    @Test
    @DisplayName("관리자 - ADMIN 전용 API 접근 성공")
    void admin_access_success() throws Exception {
        mockMvc.perform(get("/api/test/admin")
                        .header(JwtUtil.AUTHORIZATION_HEADER, validAdminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ADMIN"));
    }

    @Test
    @DisplayName("토큰이 없는 상태로 인증이 필요한 API에 접근")
    void access_fail_notoken() throws Exception {
        mockMvc.perform(get("/api/test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("만료된 토큰 - 인증 필요 API 접근 시도")
    void expiredtoken_error() throws Exception {
        mockMvc.perform(get("/api/test")
                        .header(JwtUtil.AUTHORIZATION_HEADER, expiredToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(ErrorCode.INVALID_TOKEN.toString()));
    }

    @Test
    @DisplayName("잘못된 서명 토큰 - 인증 필요 API 접근 시도")
    void invalidsignaturetoken_error() throws Exception {
        mockMvc.perform(get("/api/test")
                        .header(JwtUtil.AUTHORIZATION_HEADER, invalidSignatureToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(ErrorCode.INVALID_TOKEN.toString()));
    }

    @Test
    @DisplayName("잘못된 형식의 토큰 - 인증 필요 API 접근 시도")
    void malformedtoken_error() throws Exception {
        mockMvc.perform(get("/api/test")
                        .header(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.BEARER_PREFIX + "weird.token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(ErrorCode.INVALID_TOKEN.toString()));
    }

    @Test
    @DisplayName("유효한 토큰이지만 존재하지 않는 API 접근 시도")
    void validtoken_notfoundapi_error() throws Exception {
        mockMvc.perform(get("/api/none")
                        .header(JwtUtil.AUTHORIZATION_HEADER, validUserToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
