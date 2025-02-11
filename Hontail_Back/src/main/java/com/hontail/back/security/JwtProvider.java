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

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

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
                        return new RuntimeException("User not found with email: " + email);
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
            throw new RuntimeException("Token creation failed", e);
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
}