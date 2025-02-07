package com.hontail.back.login.controller;

import com.hontail.back.login.dto.request.LoginRequest;
import com.hontail.back.oauth.JwtOAuth2LoginService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private JwtOAuth2LoginService oAuth2UserService;

    //    @GetMapping("/login")
//    public String login() {
//        return "login";
//    }
    @PostMapping("/login")
    @Operation(description = "로그인")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        log.debug("Received login request1111: {}", request); // 요청 로그
        try {
            String jwt = oAuth2UserService.login(request.getProvider(), request.getToken());
            return ResponseEntity.ok(Collections.singletonMap("jwt", jwt));
        } catch (Exception e) {
            log.error("Exception occurred during login", e); // 예외 로그
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Internal server error"));
        }
    }
}
