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
    private static final String SECRET_KEY = "e2d567a51381b64a5068899caad46d3b9692eb32535aa7e2db999381888328b03cc555270e7fb21b680f00178a368c0c4444f8b73501591ec5901af62755cb90";
    private static final long ACCESS_TOKEN_EXPIRATION = 60 * 60 * 1000; // 1시간
    private static final long REFRESH_TOKEN_EXPIRATION = 14 * 24 * 60 * 60 * 1000; // 14일

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public String createToken(String email) {
        try {
            // 사용자 ID 가져오기
            int userId = userRepository.findByUserEmail(email)
                    .map(user -> user.getId())
                    .orElseThrow(() -> {
                        log.error("User not found with email: {}", email);
                        return new RuntimeException("User not found with email: " + email);
                    });

            // 기존 토큰 완전 삭제
            accessTokenRepository.deleteAllByUserId(userId);
            refreshTokenRepository.deleteByUserId(userId);

            // Access Token 생성 (userId 포함)
            String accessToken = createAccessToken(email, userId);
            log.info("Created access token: {}", accessToken);

            // Refresh Token 생성 (userId 포함)
            String refreshToken = createRefreshToken(email, userId);
            log.info("Created refresh token: {}", refreshToken);

            // Access Token 저장
            AccessToken accessTokenEntity = AccessToken.builder()
                    .userId(userId)
                    .token(accessToken)
                    .expiryDate(LocalDateTime.now().plusSeconds(ACCESS_TOKEN_EXPIRATION / 1000))
                    .build();
            accessTokenRepository.save(accessTokenEntity);
            log.info("Saved access token for user: {}", email);

            // Refresh Token 저장
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .userId(userId)
                    .token(refreshToken)
                    .expiryDate(LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION / 1000))
                    .build();
            refreshTokenRepository.save(refreshTokenEntity);
            log.info("Saved refresh token for user: {}", email);

            return accessToken;

        } catch (Exception e) {
            log.error("Token creation failed for email: {}", email, e);
            throw new RuntimeException("Token creation failed", e);
        }
    }

    public String createAccessToken(String email, int userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)       // 사용자 ID 추가
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(String email, int userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)       // 사용자 ID 추가
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }
}
