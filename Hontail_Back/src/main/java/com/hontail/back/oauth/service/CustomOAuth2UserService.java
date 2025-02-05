package com.hontail.back.oauth.service;

import com.hontail.back.db.entity.User;
import com.hontail.back.db.repository.UserRepository;
import com.hontail.back.oauth.dto.OAuth2UserInfoDto;
import com.hontail.back.oauth.entity.ProviderType;
import com.hontail.back.oauth.entity.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        ProviderType providerType = getProviderType(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2UserInfoDto userInfo = getOAuth2UserInfo(providerType, attributes);
        User user = saveOrUpdate(userInfo);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey())),
                attributes,
                userNameAttributeName
        );
    }

    private ProviderType getProviderType(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> ProviderType.GOOGLE;
            case "naver" -> ProviderType.NAVER;
            case "kakao" -> ProviderType.KAKAO;
            default -> throw new OAuth2AuthenticationException("Unsupported provider type");
        };
    }

    private OAuth2UserInfoDto getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
        return switch (providerType) {
            case GOOGLE -> getGoogleUserInfo(attributes);
            case NAVER -> getNaverUserInfo(attributes);
            case KAKAO -> getKakaoUserInfo(attributes);
        };
    }

    private OAuth2UserInfoDto getGoogleUserInfo(Map<String, Object> attributes) {
        return OAuth2UserInfoDto.builder()
                .providerId(attributes.get("sub") != null ? attributes.get("sub").toString() : "")
                .email(attributes.get("email") != null ? attributes.get("email").toString() : "")
                .nickname(attributes.get("name") != null ? attributes.get("name").toString() : "")
                .imageUrl(attributes.get("picture") != null ? attributes.get("picture").toString() : "")
                .providerType(ProviderType.GOOGLE)
                .build();
    }

    @SuppressWarnings("unchecked")
    private OAuth2UserInfoDto getNaverUserInfo(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            throw new OAuth2AuthenticationException("Invalid Naver response");
        }

        return OAuth2UserInfoDto.builder()
                .providerId(response.get("id") != null ? response.get("id").toString() : "")
                .email(response.get("email") != null ? response.get("email").toString() : "")
                .nickname(response.get("nickname") != null ? response.get("nickname").toString() : "")
                .imageUrl(response.get("profile_image") != null ? response.get("profile_image").toString() : "")
                .providerType(ProviderType.NAVER)
                .build();
    }

    @SuppressWarnings("unchecked")
    private OAuth2UserInfoDto getKakaoUserInfo(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;

        return OAuth2UserInfoDto.builder()
                .providerId(attributes.get("id") != null ? attributes.get("id").toString() : "")
                .email(kakaoAccount != null && kakaoAccount.get("email") != null ? kakaoAccount.get("email").toString() : "")
                .nickname(profile != null && profile.get("nickname") != null ? profile.get("nickname").toString() : "")
                .imageUrl(profile != null && profile.get("profile_image_url") != null ? profile.get("profile_image_url").toString() : "")
                .providerType(ProviderType.KAKAO)
                .build();
    }

    @Transactional
    public User saveOrUpdate(OAuth2UserInfoDto userInfo) {
        User user = userRepository.findByProviderTypeAndProviderId(
                userInfo.getProviderType(),
                userInfo.getProviderId()
        ).map(entity -> {
            entity.update(userInfo.getNickname(), userInfo.getImageUrl());
            return entity;
        }).orElseGet(() -> User.builder()
                .userEmail(userInfo.getEmail())
                .userNickname(userInfo.getNickname())
                .userImageUrl(userInfo.getImageUrl())
                .providerType(userInfo.getProviderType())
                .providerId(userInfo.getProviderId())
                .role(Role.USER)  // 기본 역할 설정
                .build());

        return userRepository.save(user);
    }
}