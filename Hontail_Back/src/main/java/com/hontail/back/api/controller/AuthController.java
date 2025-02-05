package com.hontail.back.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Hello, OAuth2 + JWT!");
    }

    @GetMapping("/user-info")
    @Operation(description = "유저정보 확인")
    public ResponseEntity<Map<String, Object>> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", oauth2User.getAttribute("email"));
        userInfo.put("name", oauth2User.getAttribute("name"));
        userInfo.put("picture", oauth2User.getAttribute("picture"));

        return ResponseEntity.ok(userInfo);
    }
}