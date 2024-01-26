package com.donguri.jejudorang.global.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.header}")
    private String tokenRequestHeader;

    @Value("${jwt.header.prefix}")
    private String tokenRequestHeaderPrefix;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    /*
    * request header의 유효 토큰 필터링
    *
    * */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("권한/인증 검증 시작 -- doFilterInternal");

        String token = getJwtFromRequest(request); // 쿠키에서 추출한 토큰
        log.info("Request Token ==== {}", token);

        try {

            if (token != null && jwtProvider.validateJwtToken(token)) {
                String username = jwtProvider.getUserNameFromJwtToken(token); // 토큰에서 externalId 추출
                log.info("Authentication User ---- {}", username);

                // 로그인
                UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username); // externalId 기반 UserDetails(사용자 상세 정보 - 권한+a) 로드
                List<GrantedAuthority> authorities = jwtProvider.getAuthoritiesFromJWT(token); // 토큰에서 사용자 권한 추출
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities); // 인증 토큰 생성

                // 인증된 사용자 설정
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (ExpiredJwtException e) {
            log.error("JWT 토큰이 만료되었습니다.");

        } catch (Exception ex) {
            log.error("Failed to set user authentication in security context: {}", ex.getMessage());
            throw ex;
        }

        filterChain.doFilter(request, response);
    }

    /*
    * 쿠키에서 토큰 추출
    * */
    private String getJwtFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("access_token")) {
                    return c.getValue();
                }
            }
        }
        return null;
    }
}
