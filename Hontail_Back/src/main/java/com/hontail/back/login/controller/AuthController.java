package com.hontail.back.login.controller;

import com.hontail.back.oauth.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 관련 API")
public class AuthController {

    private Map<String, Object> createUserInfoMap(CustomOAuth2User oauth2User) {
        return Map.of(
                "id", oauth2User.getUserId(),
                "email", oauth2User.getUserEmail(),
                "nickname", oauth2User.getName(),
                "profileImage", oauth2User.getUserImageUrl()
        );
    }

    @GetMapping("/login-success")
    @Operation(summary = "소셜 로그인 성공", description = "소셜 로그인 성공 후 사용자 정보 반환")
    public ResponseEntity<Map<String, Object>> loginSuccess(@AuthenticationPrincipal CustomOAuth2User oauth2User) {
        if (oauth2User == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(createUserInfoMap(oauth2User));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 사용자 로그아웃 처리")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // 스프링 시큐리티 컨텍스트 클리어
        SecurityContextHolder.clearContext();

        // 세션 무효화 (옵션)
        request.getSession().invalidate();
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

//    @GetMapping("/user-info")
//    @Operation(summary = "현재 사용자 정보 조회", description = "로그인된 사용자의 정보를 반환")
//    public ResponseEntity<Map<String, Object>> getCurrentUserInfo(@AuthenticationPrincipal CustomOAuth2User oauth2User) {
//        if (oauth2User == null) {
//            return ResponseEntity.status(401).body(Map.of("message", "로그인되지 않은 사용자"));
//        }
//
//        return ResponseEntity.ok(createUserInfoMap(oauth2User));
//    }
}