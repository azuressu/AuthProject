package com.auth.application.service;

import com.auth.common.exception.CustomException;
import com.auth.common.exception.ErrorCode;
import com.auth.domain.entity.User;
import com.auth.domain.entity.UserRole;
import com.auth.infrastructure.repository.UserRepository;
import com.auth.presentation.dto.AdminSignUpRequest;
import com.auth.presentation.dto.SignUpRequest;
import com.auth.presentation.dto.SignUpResponse;
import com.auth.presentation.dto.SwitchRoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${admin.key}")
    private String adminKey;

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
     * 관리자 회원가입
     *
     * @param request : 회원가입 내용 Request
     * @return : 관리자 회원가입 완료 DTO 반환
     */
    public SignUpResponse adminSignUp(AdminSignUpRequest request) {
        // 1. username이 동일한 사용자가 있는지 확인
        validateUsernameDuplication(request.getUsername());

        // 2. 관리자 암호와 일치하지 않는 경우
        if (!request.getAdminKey().equals(adminKey)) {
            throw new CustomException(ErrorCode.INVALID_ADMIN_KEY);
        }

        // 3. password 인코딩
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 4. User 생성
        User user = request.to(encodedPassword);

        // 5. User 저장
        User savedUser = userRepository.save(user);

        return SignUpResponse.from(savedUser);
    }

    /**
     * 사용자에게 Admin 권한 부여
     *
     * @param userId : 권한을 부여할 사용자
     * @param user   : 권한을 부여하는 사용자
     * @return : 권한을 변경한 사용자 정보 반환
     */
    @Transactional
    public SwitchRoleResponse switchRole(Long userId, User user) {
        // 1. 현재 사용자 검증
        if (!user.getUserRole().equals(UserRole.ADMIN)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        // 2. 권한을 변경하려는 사용자 검증
        User findUser = findUser(userId);

        // 3. 해당 사용자의 권한을 Admin으로 변경
        findUser.updateUserRole(UserRole.ADMIN);
        userRepository.save(findUser);

        // 4. 결과 반환
        return SwitchRoleResponse.from(findUser);
    }

    /**
     * 사용자 검색
     *
     * @param userId : 찾을 사용자의 ID
     * @return : 찾은 사용자 반환
     */
    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
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
