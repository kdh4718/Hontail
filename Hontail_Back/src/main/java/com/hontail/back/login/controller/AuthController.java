package com.hontail.back.login.controller;

import com.hontail.back.oauth.JwtOAuth2LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "로그인/아웃 관련 API")
public class AuthController {

    private final JwtOAuth2LoginService jwtOAuth2LoginService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/login-success")
    @Operation(summary = "소셜 로그인 성공", description = "소셜 로그인 성공 후 Access Token과 Refresh Token 반환")
    public ResponseEntity<Map<String, String>> loginSuccess(OAuth2AuthenticationToken authenticationToken) {
        log.info("로그인 성공 처리 시작");

        try {
            // 액세스 토큰 가져오기
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    authenticationToken.getAuthorizedClientRegistrationId(),
                    authenticationToken.getName()
            );

            String accessToken = client.getAccessToken().getTokenValue();
            log.info("OAuth2 액세스 토큰 획득 완료");

            // JWT 토큰 생성 (액세스 토큰과 리프레시 토큰 포함)
            Map<String, String> tokens = jwtOAuth2LoginService.login(
                    authenticationToken.getAuthorizedClientRegistrationId(),
                    accessToken
            );
            log.info("JWT 토큰 생성 완료");

            return ResponseEntity.ok(tokens);

        } catch (Exception e) {
            log.error("토큰 생성 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 사용자 로그아웃 처리")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        log.info("로그아웃 처리 시작");

        SecurityContextHolder.clearContext();
        request.getSession().invalidate();

        log.info("로그아웃 처리 완료");
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새로운 Access Token 발급")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            String newAccessToken = jwtOAuth2LoginService.refreshAccessToken(refreshToken);

            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (Exception e) {
            log.error("토큰 갱신 중 오류 발생", e);
            return ResponseEntity.status(401).build();
        }
    }
}