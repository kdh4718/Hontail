package com.hontail.back.oauth.dto;

import com.hontail.back.oauth.entity.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuth2UserInfoDto {
    private String providerId;
    private String email;
    private String nickname;
    private String imageUrl;
    private ProviderType providerType;
}