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
    public String login(String provider, String accessToken) {
        log.debug("OAuth2 로그인 시작. Provider: {}", provider);

        try {
            // 소셜 로그인 제공자로부터 사용자 정보 가져오기
            Map<String, Object> userInfo = getUserInfoFromProvider(provider, accessToken);
            log.debug("사용자 정보 획득 성공: {}", userInfo);

            // 이메일 추출
            String email = extractEmail(provider, userInfo);
            if (email == null) {
                throw new RuntimeException("OAuth2 인증에서 이메일을 찾을 수 없습니다.");
            }
            log.debug("이메일 추출 성공: {}", email);

            // 사용자 등록 또는 업데이트
            User user = registerOrUpdateUser(email, provider, userInfo);
            log.debug("사용자 정보 저장 완료. User ID: {}", user.getId());

            // JWT 토큰 생성
            String jwtToken = jwtProvider.createToken(user.getUserEmail());
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
            case "kakao" -> "https://kapi.kakao.com/v2/user/me";
            default -> throw new IllegalArgumentException("지원하지 않는 제공자입니다: " + provider);
        };

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        log.debug("사용자 정보 요청. URL: {}", url);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new org.springframework.http.HttpEntity<>(headers),
                    Map.class
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
        log.debug("사용자 정보 조회. Email: {}, Provider: {}", email, provider);

        return existingUser
                .map(user -> updateExistingUser(user, provider, attributes))
                .orElseGet(() -> createNewUser(email, provider, attributes));
    }

    private User updateExistingUser(User user, String provider, Map<String, Object> attributes) {
        user.setUserImageUrl(extractImageUrl(provider, attributes));
        user.setUserNickname(extractNickname(provider, attributes));
        User updatedUser = userRepository.save(user);
        log.debug("기존 사용자 정보 업데이트 완료. User ID: {}", updatedUser.getId());
        return updatedUser;
    }

    private User createNewUser(String email, String provider, Map<String, Object> attributes) {
        User newUser = User.builder()
                .userEmail(email)
                .userNickname(extractNickname(provider, attributes))
                .userImageUrl(extractImageUrl(provider, attributes))
                .build();

        User savedUser = userRepository.save(newUser);
        log.debug("새 사용자 등록 완료. User ID: {}", savedUser.getId());
        return savedUser;
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