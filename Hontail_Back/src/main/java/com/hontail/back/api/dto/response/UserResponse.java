package com.hontail.back.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "사용자 응답 DTO")
public class UserResponse {
    @Schema(description = "사용자 ID", example = "1")
    private Integer id;

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "사용자 닉네임", example = "홍길동")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImage;

    @Schema(description = "소셜 로그인 제공자", example = "GOOGLE")
    private String providerType;

    @Schema(description = "사용자 권한", example = "USER")
    private String role;
}