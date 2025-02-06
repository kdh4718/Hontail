package com.hontail.back.api.controller;

import com.hontail.back.oauth.CustomOAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal CustomOAuth2User oauth2User, Model model) {
        if (oauth2User != null) {
            model.addAttribute("user", oauth2User.getUserInfo());
        }
        return "home";  // templates/home.html을 렌더링
    }
}