package com.hontail.back.config;

import com.hontail.back.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.hontail.back.security.JwtProvider;
import com.hontail.back.security.JwtAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(true)
                )
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
                                        "/api/cocktail/filtering/**", // 칵테일 Basic/Custom 조회
                                        "/api/cocktail/top-liked", // 칵테일 좋아요 상위 조회
                                        "/api/cocktail/search/**", // 칵테일 검색
                                        "/api/cocktail/detail/*",  // 칵테일 상세 조회
                                        "/api/cocktail/detail/*/comments",  // 칵테일 댓글 조회
                                        "/api/bartender/**" // 바텐더 전체 기능
                                ).permitAll()
                                // 인증 필요 경로
                                .requestMatchers(
                                        "/api/cocktail/liked", // 사용자가 좋아요한 칵테일 조회
                                        "/api/cocktail/detail/*/likes", // 좋아요 추가, 제거
                                        "/api/cocktail/detail/*/comments/**", // 댓글 수정/삭제
                                        "/api/cocktail/detail/*/comment"  // 칵테일 댓글 작성
                                ).authenticated()
                                // 기타 요청은 선택적 접근
                                .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 ->
                        oauth2
                                .loginPage("/api/login/")
                                .userInfoEndpoint(userInfo ->
                                        userInfo.userService(customOAuth2UserService)
                                )
                                .defaultSuccessUrl("/api/auth/login-success", true)
                                .failureUrl("/api/login/providers?error=true")
                )
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
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:8080",   // Spring Boot 개발 서버
                "https://yourproductiondomain.com"  // 프로덕션 도메인
        ));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
                "authorization",
                "content-type",
                "x-auth-token"
        ));
        configuration.setExposedHeaders(Arrays.asList(
                "x-auth-token"
        ));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}