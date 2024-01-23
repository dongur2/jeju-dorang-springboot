package com.donguri.jejudorang.global.util;

import com.donguri.jejudorang.domain.user.entity.User;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;

import java.util.Date;
import java.util.List;

public class JwtUtil {

    @Value("${jwtutils.secret-key}")
    private String SECRET_KEY;

    private static final long EXPIRATION_TIME = 60*60*1000; // 1시간

    private final JwtParser jwtParser;

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    public JwtUtil(){
        this.jwtParser = Jwts.parser().setSigningKey(SECRET_KEY);
    }

    /*
    * createToken
    * User를 받아서 User의 데이터로 Claims 생성 => JWT Token 빌드
    * - Claims: JWT의 body로 사용
    *
    * */
    public String createToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getProfile().getExternalId());
        claims.put("email", user.getAuthentication().getEmail());

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    /*
    * resolveClaims
    * HttpRequest를 받아 요청 헤더의 Bearer 토큰에서 Claims를 확인
    *
    * */
    public Claims resolveClaims(HttpServletRequest request) {
        try {
            String token = resolveToken(request);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) { // 만료
            request.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) { // 비유효
            request.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }

    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw e;
        }
    }

    public String getExternalId(Claims claims) {
        return claims.getSubject();
    }

    private List<String> getRoles(Claims claims) {
        return (List<String>) claims.get("roles");
    }


}
