package com.donguri.jejudorang.global.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwtutils.secret-key}")
    private String SECRET_KEY;

    @Value("${jwtutils.expiration-time}")
    private long EXPIRATION_TIME;

    public String createToken(Authentication authentication) {

        UserDetailsService userPrincipal = (UserDetailsService) authentication.getPrincipal();

        return Jwts.builder() // * Claims == JWT body
                .setSubject((userPrincipal.getUsername())) // Claims 인스턴스의 sub 값 설정
                .setIssuedAt(new Date()) // Claims 인스턴스의 iat 값 설정
                .setExpiration(new Date((new Date()).getTime() + EXPIRATION_TIME)) // Claims 인스턴스의 exp 값 설정
                .signWith(key(), SignatureAlgorithm.HS256) // signWith(Key, SignatureAlgorithm): 구성된 JWT를 지정한 알고리즘을 이용해 지정한 키로 암호화
                .compact(); // JWT build
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)); // HMAC-SHA 알고리즘으로 만든 새로운 시크릿 키 리턴
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key()) // 복호화할 키 설정
                .build()
                .parseClaimsJws(token).getBody().getSubject(); // JWT의 Claims를 파싱해 body - subject(username)을 추출해 리턴
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(authToken);
            return true;
        } catch (MalformedJwtException e) { // JWT was not correctly constructed
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) { // JWT is expired
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) { // JWT in a particular format/configuration that does not match the format expected by the application
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) { // JWT Claims is empty
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }


}
