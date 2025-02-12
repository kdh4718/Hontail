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
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        log.debug("OAuth2 사용자 정보 로드: {}", oauth2User.getAttributes());

        String email = extractEmail(oauth2User);
        User user = userRepository.findByUserEmail(email)
                .map(existingUser -> {
                    // 기존 사용자의 경우 실제 이름으로 업데이트
                    String realName = extractRealName(oauth2User);
                    log.debug("기존 사용자 정보 업데이트 - Email: {}, RealName: {}", email, realName);
                    if (!realName.equals(existingUser.getUserNickname())) {
                        existingUser.setUserNickname(realName);
                        return userRepository.save(existingUser);
                    }
                    return existingUser;
                })
                .orElseGet(() -> {
                    log.debug("새로운 사용자 생성 - Email: {}", email);
                    return createUser(oauth2User);
                });

        return new CustomOAuth2User(user, oauth2User.getAttributes());
    }

    private String extractEmail(OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        if (attributes.containsKey("response")) {  // 네이버
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("email");
        }
        return oauth2User.getAttribute("email");  // 구글
    }

    private String extractRealName(OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        if (attributes.containsKey("response")) {  // 네이버
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("name");
        }
        return oauth2User.getAttribute("name");  // 구글
    }

    private User createUser(OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = extractEmail(oauth2User);
        String name = extractRealName(oauth2User);
        String imageUrl;

        if (attributes.containsKey("response")) {  // 네이버
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            imageUrl = (String) response.get("profile_image");
        } else {  // 구글
            imageUrl = oauth2User.getAttribute("picture");
        }

        User user = User.builder()
                .userEmail(email)
                .userNickname(name)
                .userImageUrl(imageUrl)
                .build();

        log.debug("새로운 사용자 생성 완료 - Email: {}, Name: {}", email, name);
        return userRepository.save(user);
    }
}