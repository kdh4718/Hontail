package com.hontail.back.oauth;

import com.hontail.back.db.entity.User;
import com.hontail.back.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;  // 이 줄 추가
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            String email = extractEmail(registrationId, oauth2User.getAttributes());

            if (email == null) {
                throw new OAuth2AuthenticationException(new OAuth2Error("email_not_found"), "Email not found from OAuth2 provider");
            }

            User user = registerOrUpdateUser(email, registrationId, oauth2User.getAttributes());
            return new CustomOAuth2User(user, oauth2User.getAttributes());

        } catch (Exception e) {
            log.error("Error during OAuth2 authentication: ", e);
            throw new OAuth2AuthenticationException(new OAuth2Error("authentication_error"), "Authentication failed");
        }
    }

    private String extractEmail(String registrationId, Map<String, Object> attributes) {
        try {
            return switch (registrationId.toLowerCase()) {
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
                default -> {
                    log.warn("Unsupported registration ID: {}", registrationId);
                    yield null;
                }
            };
        } catch (Exception e) {
            log.error("Error extracting email for provider {}: ", registrationId, e);
            return null;
        }
    }

    @Transactional
    protected User registerOrUpdateUser(String email, String registrationId, Map<String, Object> attributes) {
        return userRepository.findByUserEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, registrationId, attributes))
                .orElseGet(() -> createNewUser(email, registrationId, attributes));
    }

    private User updateExistingUser(User user, String registrationId, Map<String, Object> attributes) {
        String imageUrl = extractImageUrl(registrationId, attributes);
        if (imageUrl != null) {
            user.setUserImageUrl(imageUrl);
        }

        String nickname = extractNickname(registrationId, attributes);
        if (nickname != null) {
            user.setUserNickname(nickname);
        }

        return userRepository.save(user);
    }

    private User createNewUser(String email, String registrationId, Map<String, Object> attributes) {
        String nickname = extractNickname(registrationId, attributes);
        String imageUrl = extractImageUrl(registrationId, attributes);

        if (nickname == null) {
            nickname = "User_" + UUID.randomUUID().toString().substring(0, 8);
        }

        User newUser = User.builder()
                .userEmail(email)
                .userNickname(nickname)
                .userImageUrl(imageUrl)
                .providerId(extractProviderId(registrationId, attributes))
                .providerType(User.ProviderType.valueOf(registrationId.toUpperCase()))
                .role(User.Role.USER)
                .build();

        return userRepository.save(newUser);
    }

    private String extractProviderId(String registrationId, Map<String, Object> attributes) {
        try {
            return switch (registrationId.toLowerCase()) {
                case "google" -> (String) attributes.get("sub");
                case "naver" -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                    yield response != null ? (String) response.get("id") : null;
                }
                case "kakao" -> String.valueOf(attributes.get("id"));
                default -> null;
            };
        } catch (Exception e) {
            log.error("Error extracting provider ID for provider {}: ", registrationId, e);
            return null;
        }
    }

    private String extractNickname(String registrationId, Map<String, Object> attributes) {
        try {
            return switch (registrationId.toLowerCase()) {
                case "google" -> (String) attributes.get("name");
                case "naver" -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                    yield response != null ? (String) response.get("nickname") : null;
                }
                case "kakao" -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
                    yield properties != null ? (String) properties.get("nickname") : null;
                }
                default -> null;
            };
        } catch (Exception e) {
            log.error("Error extracting nickname for provider {}: ", registrationId, e);
            return null;
        }
    }

    private String extractImageUrl(String registrationId, Map<String, Object> attributes) {
        try {
            return switch (registrationId.toLowerCase()) {
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
        } catch (Exception e) {
            log.error("Error extracting image URL for provider {}: ", registrationId, e);
            return null;
        }
    }
}