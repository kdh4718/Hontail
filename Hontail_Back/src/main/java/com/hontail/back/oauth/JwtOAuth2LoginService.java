package com.hontail.back.oauth;

import com.hontail.back.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtOAuth2LoginService {

    private final JwtProvider jwtProvider;
    private final RestTemplate restTemplate;

    @Transactional
    public String login(String provider, String accessToken) {
        try {
            Map<String, Object> userInfo = getUserInfoFromProvider(provider, accessToken);
            String email = extractEmail(provider, userInfo);
            String nickname = extractNickname(provider, userInfo);
            String imageUrl = extractImageUrl(provider, userInfo);

            if (email == null) {
                throw new RuntimeException("OAuth2 인증에서 이메일을 찾을 수 없습니다.");
            }

            log.debug("소셜 로그인 정보 추출 - Email: {}, Nickname: {}, ProfileImage : {}", email, nickname, imageUrl);
            String jwtToken = jwtProvider.createToken(email, nickname, imageUrl);
            log.info("JWT 토큰 생성 완료");

            return jwtToken;

        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생", e);
            throw e;
        }
    }

    private Map<String, Object> getUserInfoFromProvider(String provider, String accessToken) {
        String url = switch (provider.toLowerCase()) {
            case "naver" -> "https://openapi.naver.com/v1/nid/me";
            case "google" -> "https://www.googleapis.com/oauth2/v3/userinfo";
            default -> throw new IllegalArgumentException("지원하지 않는 제공자입니다: " + provider);
        };

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        log.debug("사용자 정보 요청. URL: {}", url);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new org.springframework.http.HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {}
            );
            log.debug("소셜 로그인 제공자 응답 수신: {}", response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("사용자 정보 요청 실패. URL: {}, Error: {}", url, e.getMessage());
            throw new RuntimeException("Failed to get user info from provider", e);
        }
    }

    private String extractEmail(String provider, Map<String, Object> attributes) {
        return switch (provider.toLowerCase()) {
            case "google" -> (String) attributes.get("email");
            case "naver" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                yield response != null ? (String) response.get("email") : null;
            }
            default -> null;
        };
    }

    private String extractNickname(String provider, Map<String, Object> attributes) {
        String nickname = switch (provider.toLowerCase()) {
            case "google" -> (String) attributes.get("name");
            case "naver" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                String name = response != null ? (String) response.get("nickname") : null;
                yield name != null ? name : "Unknown User";
            }
            default -> "Unknown User";
        };
        return nickname;
    }

    private String extractImageUrl(String provider, Map<String, Object> attributes) {
        String imageUrl = switch (provider.toLowerCase()) {
            case "google" -> (String) attributes.get("name");
            case "naver" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                String image = response != null ? (String) response.get("profile_image") : null;
                yield image != null ? image : null;
            }
            default -> null;
        };
        return imageUrl;
    }

}