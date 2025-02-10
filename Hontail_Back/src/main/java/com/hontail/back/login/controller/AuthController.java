package com.hontail.back.login.controller;

import com.hontail.back.oauth.CustomOAuth2User;
import com.hontail.back.oauth.JwtOAuth2LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 관련 API")
public class AuthController {

    private final JwtOAuth2LoginService jwtOAuth2LoginService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    private Map<String, Object> createUserInfoMap(CustomOAuth2User oauth2User) {
        return Map.of(
                "id", oauth2User.getUserId(),
                "email", oauth2User.getUserEmail(),
                "nickname", oauth2User.getName(),
                "profileImage", oauth2User.getUserImageUrl()
        );
    }

    @GetMapping("/login-success")
    @Operation(summary = "소셜 로그인 성공", description = "소셜 로그인 성공 후 사용자 정보와 토큰 반환")
    public ResponseEntity<Map<String, Object>> loginSuccess(
            @AuthenticationPrincipal CustomOAuth2User oauth2User,
            OAuth2AuthenticationToken authenticationToken
    ) {
        log.info("로그인 성공 처리 시작");

        if (oauth2User == null) {
            log.error("OAuth2User is null");
            return ResponseEntity.badRequest().build();
        }

        try {
            // OAuth2 액세스 토큰 가져오기
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    authenticationToken.getAuthorizedClientRegistrationId(),
                    authenticationToken.getName()
            );

            String accessToken = client.getAccessToken().getTokenValue();
            log.info("OAuth2 액세스 토큰 획득 완료");

            // JWT 토큰 생성
            String token = jwtOAuth2LoginService.login(
                    authenticationToken.getAuthorizedClientRegistrationId(),
                    accessToken
            );
            log.info("JWT 토큰 생성 완료");

            // 응답 데이터 생성
            Map<String, Object> response = new HashMap<>(createUserInfoMap(oauth2User));
            response.put("token", token);

            return ResponseEntity.ok(response);

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

//    @GetMapping("/user-info")
//    @Operation(summary = "현재 사용자 정보 조회", description = "로그인된 사용자의 정보를 반환")
//    public ResponseEntity<Map<String, Object>> getCurrentUserInfo(@AuthenticationPrincipal CustomOAuth2User oauth2User) {
//        if (oauth2User == null) {
//            log.error("인증되지 않은 사용자");
//            return ResponseEntity.status(401).body(Map.of("message", "로그인되지 않은 사용자"));
//        }
//
//        log.info("사용자 정보 조회: {}", oauth2User.getUserEmail());
//        return ResponseEntity.ok(createUserInfoMap(oauth2User));
//    }
}