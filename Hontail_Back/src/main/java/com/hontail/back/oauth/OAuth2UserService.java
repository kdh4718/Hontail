package com.hontail.back.oauth;

import com.hontail.back.db.entity.User;
import com.hontail.back.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
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
    private static final String EMAIL_NOT_FOUND = "email_not_found";
    private static final String PROVIDER_NOT_SUPPORTED = "provider_not_supported";
    private static final String USER_CREATION_ERROR = "user_creation_error";

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        try {
            String email = extractEmail(registrationId, oauth2User.getAttributes());

            if (email == null) {
                log.error("Failed to extract email for provider: {}", registrationId);
                throw new OAuth2AuthenticationException(
                        new OAuth2Error(EMAIL_NOT_FOUND),
                        "Email not found from OAuth2 provider"
                );
            }

            User user = registerOrUpdateUser(email, registrationId, oauth2User.getAttributes());
            return new CustomOAuth2User(user, oauth2User.getAttributes());

        } catch (OAuth2AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during OAuth2 authentication: ", e);
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("authentication_error"),
                    "Authentication failed: " + e.getMessage()
            );
        }
    }

    private String extractEmail(String registrationId, Map<String, Object> attributes) {
        try {
            return switch (registrationId.toLowerCase()) {
                case "google" -> (String) attributes.get("email");
                case "naver" -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> response = attributes.get("response") != null ?
                            (Map<String, Object>) attributes.get("response") : Map.of();
                    yield response.get("email") != null ? (String) response.get("email") : null;
                }
                case "kakao" -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> kakaoAccount = attributes.get("kakao_account") != null ?
                            (Map<String, Object>) attributes.get("kakao_account") : Map.of();
                    yield kakaoAccount.get("email") != null ? (String) kakaoAccount.get("email") : null;
                }
                default -> {
                    log.warn("Unsupported registration ID: {}", registrationId);
                    throw new OAuth2AuthenticationException(
                            new OAuth2Error(PROVIDER_NOT_SUPPORTED),
                            "Provider not supported: " + registrationId
                    );
                }
            };
        } catch (Exception e) {
            log.error("Error extracting email for provider {}: ", registrationId, e);
            return null;
        }
    }

    @Transactional
    protected User registerOrUpdateUser(String email, String registrationId, Map<String, Object> attributes) {
        try {
            return userRepository.findByUserEmail(email)
                    .map(existingUser -> updateExistingUser(existingUser, registrationId, attributes))
                    .orElseGet(() -> createNewUser(email, registrationId, attributes));
        } catch (Exception e) {
            log.error("Error during user registration/update: ", e);
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(USER_CREATION_ERROR),
                    "Failed to register/update user: " + e.getMessage()
            );
        }
    }

    private User updateExistingUser(User user, String registrationId, Map<String, Object> attributes) {
        String imageUrl = extractImageUrl(registrationId, attributes);
        String nickname = extractNickname(registrationId, attributes);
        String providerId = extractProviderId(registrationId, attributes);

        if (imageUrl != null) {
            user.setUserImageUrl(imageUrl);
        }
        if (nickname != null) {
            user.setUserNickname(nickname);
        }
        if (providerId != null) {
            user.setProviderId(providerId);
        }

        log.debug("Updating existing user: {}", user.getUserEmail());
        return userRepository.save(user);
    }

    private User createNewUser(String email, String registrationId, Map<String, Object> attributes) {
        String nickname = extractNickname(registrationId, attributes);
        String imageUrl = extractImageUrl(registrationId, attributes);
        String providerId = extractProviderId(registrationId, attributes);

        if (nickname == null) {
            nickname = generateUniqueNickname();
        }

        User newUser = User.builder()
                .userEmail(email)
                .userNickname(nickname)
                .userImageUrl(imageUrl)
                .providerId(providerId)
                .providerType(User.ProviderType.valueOf(registrationId.toUpperCase()))
                .role(User.Role.USER)
                .build();

        log.debug("Creating new user with email: {}", email);
        return userRepository.save(newUser);
    }

    private String generateUniqueNickname() {
        return "User_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String extractProviderId(String registrationId, Map<String, Object> attributes) {
        try {
            return switch (registrationId.toLowerCase()) {
                case "google" -> (String) attributes.get("sub");
                case "naver" -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> response = attributes.get("response") != null ?
                            (Map<String, Object>) attributes.get("response") : Map.of();
                    yield response.get("id") != null ? (String) response.get("id") : null;
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
                    Map<String, Object> response = attributes.get("response") != null ?
                            (Map<String, Object>) attributes.get("response") : Map.of();
                    yield response.get("nickname") != null ? (String) response.get("nickname") : null;
                }
                case "kakao" -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> properties = attributes.get("properties") != null ?
                            (Map<String, Object>) attributes.get("properties") : Map.of();
                    yield properties.get("nickname") != null ? (String) properties.get("nickname") : null;
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
                    Map<String, Object> response = attributes.get("response") != null ?
                            (Map<String, Object>) attributes.get("response") : Map.of();
                    yield response.get("profile_image") != null ? (String) response.get("profile_image") : null;
                }
                case "kakao" -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> properties = attributes.get("properties") != null ?
                            (Map<String, Object>) attributes.get("properties") : Map.of();
                    yield properties.get("profile_image") != null ? (String) properties.get("profile_image") : null;
                }
                default -> null;
            };
        } catch (Exception e) {
            log.error("Error extracting image URL for provider {}: ", registrationId, e);
            return null;
        }
    }
}