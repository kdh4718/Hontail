package com.hontail.back.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @GetMapping("/api/login")
    public String login() {
        return "login";
    }

    @PostMapping("/api/login")
    @ResponseBody
    public String loginCallback() {
        return "로그인 성공";
    }
}