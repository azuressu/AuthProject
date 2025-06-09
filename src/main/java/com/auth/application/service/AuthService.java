package com.auth.application.service;

import com.auth.common.exception.CustomException;
import com.auth.common.exception.ErrorCode;
import com.auth.domain.entity.User;
import com.auth.domain.repository.UserRepository;
import com.auth.presentation.dto.SignUpRequest;
import com.auth.presentation.dto.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     *
     * @param request : 회원가입 내용 Request
     * @return : 회원가입 성공 DTO 반환
     */
    public SignUpResponse signUp(SignUpRequest request) {
        // 1. username이 동일한 사용자가 있는지 확인
        validateUsernameDuplication(request.getUsername());

        // 2. password 인코딩
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. User 생성
        User user = request.to(encodedPassword);

        // 4. User 저장
        User savedUser = userRepository.save(user);

        return SignUpResponse.from(savedUser);
    }

    /**
     * 중복된 Username을 가진 사용자가 있는지 판별
     *
     * @param username : 사용자명
     */
    private void validateUsernameDuplication(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }
    }

}
