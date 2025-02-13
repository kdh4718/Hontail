package com.hontail.back.security;

import com.hontail.back.db.entity.AccessToken;
import com.hontail.back.db.entity.RefreshToken;
import com.hontail.back.db.entity.User;
import com.hontail.back.db.repository.AccessTokenRepository;
import com.hontail.back.db.repository.RefreshTokenRepository;
import com.hontail.back.db.repository.UserRepository;
import com.hontail.back.global.exception.CustomException;
import com.hontail.back.global.exception.ErrorCode;
import com.hontail.back.oauth.CustomOAuth2User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

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
    public String createToken(String email, String nickname, String imageUrl) {
        try {
            var user = userRepository.findByUserEmail(email)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setUserEmail(email);
                        newUser.setUserNickname(nickname);
                        newUser.setUserImageUrl(imageUrl);
                        return userRepository.save(newUser);
                    });

            int userId = user.getId();

            // 기존 리프레시 토큰 확인
            Optional<RefreshToken> existingRefreshTokenOpt = refreshTokenRepository.findByUserId(userId);
            String refreshToken;

            if (existingRefreshTokenOpt.isPresent() &&
                    existingRefreshTokenOpt.get().getExpiryDate().isAfter(LocalDateTime.now())) {
                // 유효한 리프레시 토큰이 있으면 재사용
                refreshToken = existingRefreshTokenOpt.get().getToken();
                log.debug("Reusing existing refresh token for user: {}", email);
            } else {
                // 리프레시 토큰이 없거나 만료된 경우 새로 생성
                refreshToken = createRefreshToken(email, userId, nickname);
                log.debug("Creating new refresh token for user: {}", email);

                // 기존 리프레시 토큰 삭제 (있는 경우)
                existingRefreshTokenOpt.ifPresent(refreshTokenRepository::delete);

                // 새 리프레시 토큰 저장
                RefreshToken refreshTokenEntity = RefreshToken.builder()
                        .userId(userId)
                        .token(refreshToken)
                        .expiryDate(LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION / 1000))
                        .build();
                refreshTokenRepository.save(refreshTokenEntity);
            }

            // 액세스 토큰은 항상 새로 생성
            String accessToken = createAccessToken(email, userId, nickname);
            log.debug("Access token created for user: {}", email);

            // 이전 액세스 토큰 삭제 및 새 토큰 저장
            accessTokenRepository.deleteAllByUserId(userId);
            AccessToken accessTokenEntity = AccessToken.builder()
                    .userId(userId)
                    .token(accessToken)
                    .expiryDate(LocalDateTime.now().plusSeconds(ACCESS_TOKEN_EXPIRATION / 1000))
                    .build();
            accessTokenRepository.save(accessTokenEntity);

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
                .claim("user_id", userId)
                .claim("user_nickname", userNickname)
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
                .claim("user_id", userId)
                .claim("user_nickname", userNickname)
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

        User user = userRepository.findByUserEmail(claims.get("user_email", String.class))
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("user_id", claims.get("user_id", Integer.class));
        attributes.put("user_email", claims.get("user_email", String.class));
        attributes.put("user_nickname", claims.get("user_nickname", String.class));

        CustomOAuth2User principal = new CustomOAuth2User(user, attributes);

        return new UsernamePasswordAuthenticationToken(
                principal,
                token,
                Collections.emptyList()
        );
    }

    @Transactional
    public String refreshAccessToken(String refreshToken) {
        try {
            // Refresh 토큰 검증
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();

            String type = claims.get("type", String.class);
            if (!"refresh".equals(type)) {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }

            // DB에서 리프레시 토큰 확인
            RefreshToken storedRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

            if (storedRefreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                refreshTokenRepository.delete(storedRefreshToken);
                throw new CustomException(ErrorCode.TOKEN_EXPIRED);
            }

            // 새 액세스 토큰 생성
            String email = claims.get("user_email", String.class);
            Integer userId = claims.get("user_id", Integer.class);
            String nickname = claims.get("user_nickname", String.class);

            String newAccessToken = createAccessToken(email, userId, nickname);

            // 이전 액세스 토큰 삭제 및 새 토큰 저장
            accessTokenRepository.deleteAllByUserId(userId);
            AccessToken accessTokenEntity = AccessToken.builder()
                    .userId(userId)
                    .token(newAccessToken)
                    .expiryDate(LocalDateTime.now().plusSeconds(ACCESS_TOKEN_EXPIRATION / 1000))
                    .build();
            accessTokenRepository.save(accessTokenEntity);

            return newAccessToken;
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.TOKEN_CREATION_FAILED);
        }
    }
}