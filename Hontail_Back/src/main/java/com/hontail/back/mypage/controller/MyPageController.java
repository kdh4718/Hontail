//package com.hontail.back.mypage.controller;
//
//import com.hontail.back.mypage.dto.UpdateNicknameRequest;
//import com.hontail.back.mypage.dto.UserResponse;
//import com.hontail.back.mypage.service.MyPageService;
//import com.hontail.back.oauth.CustomOAuth2User;
//import io.swagger.v3.oas.annotations.Operation;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Collections;
//
//@RestController
//@RequestMapping("/api/mypage")
//@RequiredArgsConstructor
//public class MyPageController {
//
//    private static final Logger log = LoggerFactory.getLogger(MyPageController.class);
//    private final MyPageService myPageService;
//
//    @GetMapping("/me")
//    @Operation(description = "현재 사용자 정보 가져오기")
//    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
//        log.debug("현재 사용자 정보 요청 - OAuth2User: {}", oAuth2User);
//
//        try {
//            String email = oAuth2User.getUser().getUserEmail();
//            log.debug("사용자 이메일 추출 - Email: {}", email);
//
//            UserResponse userResponse = myPageService.getCurrentUser(email);
//            log.info("사용자 정보 조회 성공 - Email: {}", email);
//
//            return ResponseEntity.ok(userResponse);
//        } catch (Exception e) {
//            log.error("현재 사용자 정보 조회 중 오류 발생", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Collections.singletonMap("error", "Internal server error"));
//        }
//    }
//
//    @PutMapping("/me/nickname")
//    @Operation(description = "닉네임 수정")
//    public ResponseEntity<?> updateNickname(
//            @RequestBody UpdateNicknameRequest request,
//            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
//        log.debug("닉네임 변경 요청 - Request: {}, OAuth2User: {}", request, oAuth2User);
//
//        try {
//            String email = oAuth2User.getUser().getUserEmail();
//            log.debug("사용자 이메일 추출 - Email: {}", email);
//
//            UserResponse updatedUser = myPageService.updateNickname(email, request);
//            log.info("닉네임 변경 성공 - Email: {}, New Nickname: {}", email, updatedUser.getNickname());
//
//            return ResponseEntity.ok(updatedUser);
//        } catch (Exception e) {
//            log.error("닉네임 변경 중 오류 발생", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Collections.singletonMap("error", "Internal server error"));
//        }
//    }
//}

package com.hontail.back.mypage.controller;

import com.hontail.back.mypage.dto.UpdateNicknameRequest;
import com.hontail.back.mypage.dto.UserResponse;
import com.hontail.back.mypage.service.MyPageService;
import com.hontail.back.oauth.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private static final Logger log = LoggerFactory.getLogger(MyPageController.class);
    private final MyPageService myPageService;

    @GetMapping("/me")
    @Operation(description = "현재 사용자 정보 가져오기")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        log.debug("현재 사용자 정보 요청 - OAuth2User: {}", oAuth2User);

        try {
            String email = oAuth2User.getUser().getUserEmail();
            log.debug("사용자 이메일 추출 - Email: {}", email);

            UserResponse userResponse = myPageService.getCurrentUser(email);
            log.info("사용자 정보 조회 성공 - Email: {}", email);

            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            log.error("현재 사용자 정보 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Internal server error"));
        }
    }

    @PutMapping("/me/nickname")
    @Operation(description = "닉네임 수정")
    public ResponseEntity<?> updateNickname(
            @RequestBody UpdateNicknameRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        log.debug("닉네임 변경 요청 - Request: {}, OAuth2User: {}", request, oAuth2User);

        try {
            String email = oAuth2User.getUser().getUserEmail();
            log.debug("사용자 이메일 추출 - Email: {}", email);

            UserResponse updatedUser = myPageService.updateNickname(email, request);
            log.info("닉네임 변경 성공 - Email: {}, New Nickname: {}", email, updatedUser.getNickname());

            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("닉네임 변경 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Internal server error"));
        }
    }
}