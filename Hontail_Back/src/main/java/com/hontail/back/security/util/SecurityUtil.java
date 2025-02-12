package com.hontail.back.security.util;

import com.hontail.back.global.exception.CustomException;
import com.hontail.back.global.exception.ErrorCode;
import com.hontail.back.oauth.CustomOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private SecurityUtil() { }

    public static Integer getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나 익명 사용자
        if (authentication == null || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        // OAuth2 인증 사용자
        if (authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            return oAuth2User.getUserId();
        }

        // 예상치 못한 인증 방식인 경우
        throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
    }
}