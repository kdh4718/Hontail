package com.hontail.back.login.controller;

import com.hontail.back.login.dto.request.LoginRequest;
import com.hontail.back.oauth.JwtOAuth2LoginService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@CrossOrigin("*")
@RequestMapping("/api")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private JwtOAuth2LoginService oAuth2UserService;

    @GetMapping("/login")
    public String loginPage() {
        log.info("로그인 페이지 접속 시도");
        return "login";
    }

    @PostMapping("/login")
    @ResponseBody
    @Operation(description = "로그인")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        log.debug("Received login request: {}", request);
        try {
            Map<String, String> tokens = oAuth2UserService.login(request.getProvider(), request.getToken());
            log.info("JWT 토큰 생성 성공");
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            log.error("로그인 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }
}