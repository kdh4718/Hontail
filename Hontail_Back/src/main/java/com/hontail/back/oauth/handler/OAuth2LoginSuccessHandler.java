package com.hontail.back.oauth.handler;

import com.hontail.back.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        try {
            // OAuth2 인증 토큰으로 캐스팅
            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

            // 상세 로깅
            log.error("OAuth2 Login Success");
            log.error("Authentication Type: {}", authentication.getClass().getName());
            log.error("Provider: {}", oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
            log.error("Principal Details: {}", oAuth2AuthenticationToken.getPrincipal());

            // JWT 토큰 생성
            String token = tokenProvider.generateToken(authentication);

            // 토큰 로깅
            log.error("Generated JWT Token: {}", token);

            // 세션에 토큰 저장 (선택사항)
            request.getSession().setAttribute("token", token);

            // 홈 페이지로 리다이렉트
            response.sendRedirect("/home");

        } catch (Exception e) {
            log.error("OAuth2 Login Error", e);
            // 에러 발생 시 로그인 페이지로 리다이렉트
            response.sendRedirect("/login?error=true");
        }
    }
}