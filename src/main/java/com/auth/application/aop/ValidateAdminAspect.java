package com.auth.application.aop;

import java.util.Arrays;

import com.auth.common.exception.CustomException;
import com.auth.infrastructure.security.UserDetailsImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import static com.auth.common.exception.ErrorCode.ACCESS_DENIED;
import static com.auth.common.exception.ErrorCode.INVALID_TOKEN;

@Aspect
@Component
@Slf4j
public class ValidateAdminAspect {

    @Around("@annotation(validateUser)")
    public Object validateUserRole(ProceedingJoinPoint joinPoint, ValidateAdmin validateAdmin) throws Throwable {
        // 현재 Authentication 객체에서 principal 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // principal이 null이거나, UserDetails가 아니라면
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            log.warn("인증 객체가 없습니다.");
            throw new CustomException(INVALID_TOKEN);
        }

        // 사용자 역할 확인
        String currentRole = userDetails.getUser().getUserRole().name();
        String[] allowedRoles = validateAdmin.roles();

        boolean authorized = Arrays.stream(allowedRoles)
                .anyMatch(role -> role.equalsIgnoreCase(currentRole));

        if (!authorized) {
            log.warn("Access denied. userId: {}, role: {}, allowedRoles: {}", userDetails.getUsername(), currentRole, Arrays.toString(allowedRoles));
            throw new CustomException(ACCESS_DENIED);
        }

        return joinPoint.proceed();
    }
}

