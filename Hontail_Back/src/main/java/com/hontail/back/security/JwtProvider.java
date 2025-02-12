package com.hontail.back.security;

import com.hontail.back.db.entity.AccessToken;
import com.hontail.back.db.entity.RefreshToken;
import com.hontail.back.db.repository.AccessTokenRepository;
import com.hontail.back.db.repository.RefreshTokenRepository;
import com.hontail.back.db.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import java.util.Collections;
import com.hontail.back.oauth.CustomOAuth2User;
import com.hontail.back.db.entity.User;
import com.hontail.back.global.exception.CustomException;
import com.hontail.back.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private static final String SECRET_KEY = "{JWT_SECRET}";
    private static final long ACCESS_TOKEN_EXPIRATION = 60 * 60 * 1000; // 1시간
    private static final long REFRESH_TOKEN_EXPIRATION = 14 * 24 * 60 * 60 * 1000; // 14일

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public String createToken(String email, String nickname) {
        try {
            var user = userRepository.findByUserEmail(email)
                    .orElseThrow(() -> {
                        log.error("User not found with email: {}", email);
                        return new CustomException(ErrorCode.USER_NOT_FOUND);
                    });

            int userId = user.getId();
            // 소셜 로그인에서 받아온 닉네임 사용
            String userNickname = nickname;

            // 기존 토큰 삭제
            accessTokenRepository.deleteAllByUserId(userId);
            refreshTokenRepository.deleteByUserId(userId);

            // Access Token 생성
            String accessToken = createAccessToken(email, userId, userNickname);
            log.debug("Access token created for user: {}", email);

            // Refresh Token 생성
            String refreshToken = createRefreshToken(email, userId, userNickname);
            log.debug("Refresh token created for user: {}", email);

            // Access Token 저장
            AccessToken accessTokenEntity = AccessToken.builder()
                    .userId(userId)
                    .token(accessToken)
                    .expiryDate(LocalDateTime.now().plusSeconds(ACCESS_TOKEN_EXPIRATION / 1000))
                    .build();
            accessTokenRepository.save(accessTokenEntity);

            // Refresh Token 저장
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .userId(userId)
                    .token(refreshToken)
                    .expiryDate(LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION / 1000))
                    .build();
            refreshTokenRepository.save(refreshTokenEntity);

            return accessToken;

        } catch (Exception e) {
            log.error("Token creation failed for email: {}", email, e);
            throw new CustomException(ErrorCode.TOKEN_CREATION_FAILED);
        }
    }

    private String createAccessToken(String email, int userId, String userNickname) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION);

        return Jwts.builder()
                .claim("user_email", email)
                .claim("user_id", userId)         // userId -> user_id로 변경
                .claim("user_nickname", userNickname)  // nickname -> user_nickname으로 변경
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    private String createRefreshToken(String email, int userId, String userNickname) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION);

        return Jwts.builder()
                .claim("user_email", email)
                .claim("user_id", userId)         // userId -> user_id로 변경
                .claim("user_nickname", userNickname)  // nickname -> user_nickname으로 변경
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }
    
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // User 객체 조회
        User user = userRepository.findByUserEmail(claims.get("user_email", String.class))
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // OAuth2User attributes 맵 생성
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("user_id", claims.get("user_id", Integer.class));
        attributes.put("user_email", claims.get("user_email", String.class));
        attributes.put("user_nickname", claims.get("user_nickname", String.class));

        // CustomOAuth2User 생성 -> User 객체, attributes 맵 전달
        CustomOAuth2User principal = new CustomOAuth2User(user, attributes);

        return new UsernamePasswordAuthenticationToken(
                principal,
                token,
                Collections.emptyList()
        );
    }
}