package com.hontail.back.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {
    private static final String SECRET_KEY = "e2d567a51381b64a5068899caad46d3b9692eb32535aa7e2db999381888328b03cc555270e7fb21b680f00178a368c0c4444f8b73501591ec5901af62755cb90";  // 보안을 위해 환경 변수로 관리하는 것이 좋음
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;  // 1시간

    public String createToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
