package com.hontail.back.config;

import com.hontail.back.oauth.CustomOAuth2UserService;
import com.hontail.back.security.JwtAuthenticationFilter;
import com.hontail.back.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS, CSRF, 세션 관리 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(true)
                )
                // URL 접근 권한 설정
                .authorizeHttpRequests(auth ->
                        auth
                                // 완전 공개 경로
                                .requestMatchers(
                                        "/",
                                        "/error",
                                        "/api/login/**",
                                        "/api/users/public-profile",
                                        "/api/home",
                                        "/api/public-info",
                                        "/login/**",
                                        "/oauth2/**",
                                        "/login/oauth2/code/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-resources/**",
                                        "/api/cocktail/filtering/**",
                                        "/api/cocktail/top-liked",
                                        "/api/cocktail/search/**",
                                        "/api/cocktail/detail/*",
                                        "/api/cocktail/detail/*/comments",
                                        "/api/bartender/**",
                                        "/api/auth/**"  // 로그인 성공/실패 처리를 위해 추가
                                ).permitAll()
                                // 인증 필요 경로
                                .requestMatchers(
                                        "/api/cocktail/liked",
                                        "/api/cocktail/detail/*/likes",
                                        "/api/cocktail/detail/*/comments/**",
                                        "/api/cocktail/detail/*/comment"
                                ).authenticated()
                                // 그 외 모든 요청 허용
                                .anyRequest().permitAll()
                )
                // JWT 인증 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class)
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/api/login/")
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("/api/auth/login-success", true)  // true는 항상 이 URL로 리다이렉트하도록 강제
                        .failureUrl("/api/login/providers?error=true")
                )
                // 로그아웃 설정
                .logout(logout ->
                        logout
                                .logoutSuccessUrl("/api/home")
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList( // "*" 대신 allowedOriginPatterns 사용
                "http://localhost:8080",
                "http://localhost:3000",
                "https://i12d207.p.ssafy.io",
                "http://localhost:9090"
        ));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
                "authorization",
                "content-type",
                "x-auth-token"
        ));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}