package com.hontail.back.oauth;

import com.hontail.back.db.entity.User;
import com.hontail.back.db.repository.UserRepository;
import com.hontail.back.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtOAuth2LoginService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RestTemplate restTemplate;

    @Transactional
    public String login(String provider, String token) {
        log.debug("Received token: {}", token);

        Map<String, Object> userInfo = getUserInfoFromProvider(provider, token);
        String email = extractEmail(provider, userInfo);
        if (email == null) {
            throw new RuntimeException("OAuth2 인증에서 이메일을 찾을 수 없습니다.");
        }

        User user = registerOrUpdateUser(email, provider, userInfo);
        return jwtProvider.createToken(user.getUserEmail());
    }

    private Map<String, Object> getUserInfoFromProvider(String provider, String token) {
        String url = switch (provider.toLowerCase()) {
            case "naver" -> "https://openapi.naver.com/v1/nid/me";
            case "google" -> "https://www.googleapis.com/oauth2/v3/userinfo";
            case "kakao" -> "https://kapi.kakao.com/v2/user/me";
            default -> throw new IllegalArgumentException("지원하지 않는 제공자입니다: " + provider);
        };

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        try {
            // 요청을 보내기 전에 로그
            log.debug("Sending request to {} with token: {}", url, token);

            // 요청 보내기
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, new org.springframework.http.HttpEntity<>(headers), Map.class);

            // API 응답 로그
            log.debug("Received response from {}: {}", url, response.getBody());

            return response.getBody();
        } catch (HttpClientErrorException e) {
            // 에러가 발생한 경우
            log.error("Error occurred while requesting user info from {}: {}", url, e.getMessage());
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
            case "kakao" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                yield kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
            }
            default -> null;
        };
    }

    private User registerOrUpdateUser(String email, String provider, Map<String, Object> attributes) {
        Optional<User> existingUser = userRepository.findByUserEmail(email);
        log.debug("Information {}, {}, {}", email, provider, attributes);
        return existingUser.map(user -> updateExistingUser(user, provider, attributes))
                .orElseGet(() -> createNewUser(email, provider, attributes));
    }

    private User updateExistingUser(User user, String provider, Map<String, Object> attributes) {
        user.setUserImageUrl(extractImageUrl(provider, attributes));
        user.setUserNickname(extractNickname(provider, attributes));
        return userRepository.save(user);
    }

    private User createNewUser(String email, String provider, Map<String, Object> attributes) {
        User newUser = User.builder()
                .userEmail(email)
                .userNickname(extractNickname(provider, attributes))
                .userImageUrl(extractImageUrl(provider, attributes))
                .build();
        log.debug("NewUser {}, {}, {}, {}", newUser.getUserNickname(), newUser.getId(), newUser.getUserEmail(), newUser.getUserImageUrl());
        return userRepository.save(newUser);
    }

    private String extractNickname(String provider, Map<String, Object> attributes) {
        return switch (provider.toLowerCase()) {
            case "google" -> (String) attributes.get("name");
            case "naver" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                yield response != null ? (String) response.get("name") : null;
            }
            case "kakao" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
                yield properties != null ? (String) properties.get("nickname") : null;
            }
            default -> null;
        };
    }

    private String extractImageUrl(String provider, Map<String, Object> attributes) {
        return switch (provider.toLowerCase()) {
            case "google" -> (String) attributes.get("picture");
            case "naver" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                yield response != null ? (String) response.get("profile_image") : null;
            }
            case "kakao" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
                yield properties != null ? (String) properties.get("profile_image") : null;
            }
            default -> null;
        };
    }
}
