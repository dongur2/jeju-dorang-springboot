package com.donguri.jejudorang.global.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    private static final String AUTHORITIES_CLAIM = "authorities";
    private final String jwtSecret;
    private final long jwtExpirationInMs;

    public JwtProvider(@Value("${jwt.secret-key}") String jwtSecret, @Value("${jwt.expiration-time}") long jwtExpirationInMs) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationInMs = jwtExpirationInMs;
    }


    /*
    * Principal Object로부터 새로운 JWT 생성
    *
    * */
    public String generateToken(JwtUserDetails userDetails) {
        Instant expireDate = Instant.now().plusMillis(jwtExpirationInMs);
        String authorities = getUserAuthorities(userDetails);

        return Jwts.builder() // * Claims == JWT body (토큰에 포함되는 속성/정보)
                .setSubject(Long.toString(userDetails.getId())) // Claims 인스턴스의 sub 값 설정
                .setIssuedAt(Date.from(Instant.now())) // Claims 인스턴스의 iat 값 설정
                .setExpiration(Date.from(expireDate)) // Claims 인스턴스의 exp 값 설정
                .signWith(key(), SignatureAlgorithm.HS512) // signWith(SignatureAlgorithm, String): 구성된 JWT를 지정한 알고리즘을 이용해 암호화
                .claim(AUTHORITIES_CLAIM, authorities) // JWT Claims에 커스텀 필드, 필드값 지정 - 권한 필드
                .compact(); // JWT build
    }

    /*
     * Principal Object로부터 새로운 JWT 생성
     *
     * */
    public String generateTokenFromUserId(Long userId) {
        Instant expireDate = Instant.now().plusMillis(jwtExpirationInMs);
        return Jwts.builder()
                .setSubject(Long.toString(userId))
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(expireDate))
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    /*
    * HMAC-SHA 알고리즘으로 만든 새로운 시크릿 키 리턴
    *
    * */
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /*
    * 토큰 userId 리턴
    *
    * */
    public Long getUserIdFromJWT(String token) {
        return Long.parseLong(
                Jwts.parserBuilder() // 복호화할 키 설정
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token).getBody().getSubject() // JWT의 Claims를 파싱해 body - subject(userId)을 추출해 리턴
        );
    }

    /*
    * 토큰 만료날짜 리턴
    *
    * */
    public Date getTokenExpirationFromJWT(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token).getBody().getExpiration();
    }

    /*
    * 토큰 유효기간 리턴
    *
    * */
    public long getExpiryDuration() {
        return jwtExpirationInMs;
    }

    /*
    * 토큰의 사용자 권한(Claims - authority) 리턴
    *
    * - GrantedAuthority: Spring Security에서 권한 정보를 나타내는 인터페이스
    * - SimpleGrantedAuthority: GrantedAuthority의 구현체 - 단순 문자열 -> 권한
    *
    * */
    public List<GrantedAuthority> getAuthoritiesFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Arrays.stream(claims.get(AUTHORITIES_CLAIM).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /*
    * 유저 권한(authority)를 추출하기 위한 메서드 - private
    *
    * */
    private String getUserAuthorities(JwtUserDetails userDetails) {
        return userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }


}
