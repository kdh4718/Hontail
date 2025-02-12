package com.hontail.back.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    // 기본 리다이렉트 URL (application.properties 또는 .yml 파일에 설정: oauth2.redirect-uri)
    @Value("${oauth2.redirect-uri}")
    private String redirectUri;

    // 소셜 로그인 처리 후 JWT 토큰을 생성하는 서비스
    private final JwtOAuth2LoginService jwtOAuth2LoginService;

    // OAuth2AuthorizedClientService를 이용해 access token 등 클라이언트 정보를 가져옴
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 인증 객체를 OAuth2AuthenticationToken으로 캐스팅
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String provider = authToken.getAuthorizedClientRegistrationId();
        log.debug("OAuth2 로그인 성공, provider: {}", provider);

        // OAuth2AuthorizedClientService를 통해 해당 공급자의 클라이언트 정보를 조회 (access token 포함)
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                provider, authToken.getName());
        if (authorizedClient == null) {
            log.error("OAuth2AuthorizedClient를 찾을 수 없습니다. provider: {}", provider);
            response.sendRedirect(redirectUri + "?error=clientNotFound");
            return;
        }

        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        log.debug("provider: {}의 access token: {}", provider, accessToken);

        // JwtOAuth2LoginService를 통해 JWT 토큰 생성
        String jwtToken = jwtOAuth2LoginService.login(provider, accessToken);
        log.info("JWT 토큰 생성 완료: {}", jwtToken);

        // 예시로, 기본 리다이렉트 URL에 JWT 토큰과 공급자 정보를 쿼리 파라미터로 추가하여 클라이언트를 리다이렉트
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", jwtToken)
                .queryParam("provider", provider)
                .build().toUriString();
        log.info("리다이렉트할 URL: {}", targetUrl);

        response.sendRedirect(targetUrl);
    }
}
