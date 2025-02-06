package com.hontail.back.api.controller;

import com.hontail.back.api.dto.response.UserResponse;
import com.hontail.back.db.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 정보 및 인증 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Operation(summary = "현재 사용자 정보 조회", description = "로그인한 사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content
            )
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user) {

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .email(user.getUserEmail())
                .nickname(user.getUserNickname())
                .profileImage(user.getUserImageUrl())
                .providerType(user.getProviderType().name())
                .role(user.getRole().name())
                .build();

        return ResponseEntity.ok(response);
    }
}