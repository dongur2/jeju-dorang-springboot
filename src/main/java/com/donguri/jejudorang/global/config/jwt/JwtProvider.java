package com.donguri.jejudorang.global.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
public class JwtProvider {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private static final String AUTHORITIES_CLAIM = "authorities";
    private static final String ID_CLAIM = "id";
    private final long jwtAccessExpirationInMs;
    private final long jwtRefreshExpirationInMs;

    public JwtProvider(@Value("${jwt.expiration-time.access}") long jwtAccessExpirationInMs,
                       @Value("${jwt.expiration-time.refresh}") long jwtRefreshExpirationInMs) {
        this.jwtAccessExpirationInMs = jwtAccessExpirationInMs;
        this.jwtRefreshExpirationInMs = jwtRefreshExpirationInMs;
    }

    // Authentication -> Access Token 생성
    public String generateAccessToken(Authentication authentication) {
        JwtUserDetails userPrincipal = (JwtUserDetails) authentication.getPrincipal();
        String authorities = getUserAuthorities(userPrincipal);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername()) // Claims 인스턴스의 sub 값 설정
                .setIssuedAt(new Date()) // Claims 인스턴스의 iat 값 설정
                .setExpiration(new Date((new Date()).getTime() + jwtAccessExpirationInMs)) // Claims 인스턴스의 exp 값 설정
                .signWith(key) // signWith(SignatureAlgorithm, String): 구성된 JWT를 지정한 알고리즘을 이용해 암호화
                .claim(AUTHORITIES_CLAIM, authorities) // JWT Claims에 커스텀 필드, 필드값 지정 - 권한 필드
                .claim(ID_CLAIM, userPrincipal.getId())
                .compact(); // JWT build
    }

    // Refresh Token 생성
    public String generateRefreshTokenFromUserId(Authentication authentication) {
        JwtUserDetails userPrincipal = (JwtUserDetails) authentication.getPrincipal();
        String authorities = getUserAuthorities(userPrincipal);
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtRefreshExpirationInMs))
                .claim(AUTHORITIES_CLAIM, authorities)
                .claim(ID_CLAIM, userPrincipal.getId())
                .signWith(key)
                .compact();
    }

    /*
    * JWT에서 externalId 추출
    * */
    public String getUserNameFromJwtToken(String token) { // externalId
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /*
    * JWT에서 Id 추출
    * */
    public Long getIdFromJwtToken(String token) {
        String stringId = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody().get(ID_CLAIM).toString();

        return Long.parseLong(stringId);
    }



    /*
    * 토큰 만료날짜 리턴
    *
    * */
    public Date getTokenExpirationFromJWT(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token).getBody().getExpiration();
    }

    /*
    * 토큰 유효기간 리턴
    *
    * */
    public long getExpiryDuration() {
        return jwtAccessExpirationInMs;
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
                .setSigningKey(key)
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

    /*
    * 토큰 검증
    * */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parse(authToken);
            return true;

        } catch (SignatureException e) {
            log.error("Unmatched JWT Signature");
            throw e;

        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token");
            throw e;

        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token");
            throw e;

        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token");
            throw e;

        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty.");
            throw e;
        }
    }

}
