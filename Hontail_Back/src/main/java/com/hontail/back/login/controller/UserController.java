//package com.hontail.back.login.controller;
//
//import com.hontail.back.login.dto.response.UserResponse;
//import com.hontail.back.db.entity.User;
//import com.hontail.back.oauth.CustomOAuth2User;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@Slf4j
//@Tag(name = "User", description = "유저 정보 API")
//@RestController
//@RequestMapping("/api/user")
//@RequiredArgsConstructor
//public class UserController {
//
//    @Operation(summary = "현재 사용자 정보 조회", description = "로그인한 사용자의 정보를 조회합니다.")
//    @ApiResponses({
//            @ApiResponse(
//                    responseCode = "200",
//                    description = "조회 성공",
//                    content = @Content(schema = @Schema(implementation = UserResponse.class))
//            ),
//            @ApiResponse(
//                    responseCode = "401",
//                    description = "인증되지 않은 사용자",
//                    content = @Content
//            )
//    })
//    @GetMapping("/me")
//    public ResponseEntity<UserResponse> getCurrentUser(
//            @Parameter(hidden = true)
//            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
//
//        User user = oAuth2User.getUser();
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//
//        String realName;
//        if (attributes.containsKey("response")) {  // 네이버 로그인
//            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
//            realName = (String) response.get("name");
//            log.debug("네이버 로그인 사용자 이름: {}", realName);
//        } else {  // 구글 로그인
//            realName = (String) attributes.get("name");
//            log.debug("구글 로그인 사용자 이름: {}", realName);
//        }
//
//        UserResponse response = UserResponse.builder()
//                .id(user.getId())
//                .email(user.getUserEmail())
//                .nickname(realName)
//                .profileImage(user.getUserImageUrl())
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//}

package com.hontail.back.login.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "User", description = "유저 정보 API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
}
