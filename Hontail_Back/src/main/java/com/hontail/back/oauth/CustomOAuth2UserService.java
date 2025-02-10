package com.hontail.back.oauth;

import com.hontail.back.db.entity.User;
import com.hontail.back.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        log.info("OAuth2 로그인 성공: {}", oauth2User.getAttributes());

        // 여기서 User 엔티티를 찾거나 생성
        String email = extractEmail(oauth2User);
        User user = userRepository.findByUserEmail(email)
                .orElseGet(() -> createUser(oauth2User));

        // CustomOAuth2User 객체를 반환
        return new CustomOAuth2User(user, oauth2User.getAttributes());
    }

    private String extractEmail(OAuth2User oauth2User) {
        // 네이버 로그인의 경우
        if (oauth2User.getAttributes().containsKey("response")) {
            return ((Map<String, Object>) oauth2User.getAttributes().get("response")).get("email").toString();
        }
        // 다른 소셜 로그인의 경우에 대한 처리도 추가 필요
        return oauth2User.getAttribute("email");
    }

    private User createUser(OAuth2User oauth2User) {
        // OAuth2User의 정보를 기반으로 새 User 생성
        User user = User.builder()
                .userEmail(extractEmail(oauth2User))
                .userNickname(extractNickname(oauth2User))
                .userImageUrl(extractImageUrl(oauth2User))
                .build();
        return userRepository.save(user);
    }

    private String extractNickname(OAuth2User oauth2User) {
        // 네이버 로그인의 경우
        if (oauth2User.getAttributes().containsKey("response")) {
            return ((Map<String, Object>) oauth2User.getAttributes().get("response")).get("nickname").toString();
        }
        return oauth2User.getAttribute("name");
    }

    private String extractImageUrl(OAuth2User oauth2User) {
        // 네이버 로그인의 경우
        if (oauth2User.getAttributes().containsKey("response")) {
            return ((Map<String, Object>) oauth2User.getAttributes().get("response")).get("profile_image").toString();
        }
        return oauth2User.getAttribute("picture");
    }
}