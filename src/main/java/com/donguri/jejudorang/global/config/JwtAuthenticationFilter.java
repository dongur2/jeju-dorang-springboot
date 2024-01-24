package com.donguri.jejudorang.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
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
    private JwtTokenValidator jwtTokenValidator;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /*
    * request header의 유효 토큰 필터링
    *
    * */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request); // request로부터 토큰 추출

            if (StringUtils.hasText(jwt) && jwtTokenValidator.validateToken(jwt)) { // 토큰 존재 & 유효성 검증

                Long userId = jwtProvider.getUserIdFromJWT(jwt); // 토큰에서 userId 추출
                UserDetails userDetails = customUserDetailsService.loadUserById(userId); // userId 기반 UserDetails(사용자 상세 정보 - 권한+a) 로드
                List<GrantedAuthority> authorities = jwtProvider.getAuthoritiesFromJWT(jwt); // 토큰에서 사용자 권한 추출
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, jwt, authorities); // 인증 토큰 생성

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContextHolder에 인증 토큰 설정 => 인증된 사용자 정보 설정

            }

        } catch (Exception ex) {
            log.error("Failed to set user authentication in security context: {}", ex.getMessage());
            throw ex;
        }

        filterChain.doFilter(request, response);
    }

    /*
    * request header에서 토큰 추출
    *
    * */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(tokenRequestHeader);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenRequestHeaderPrefix)) {
            log.info("Extracted Token: {}", bearerToken);
            return bearerToken.replace(tokenRequestHeaderPrefix, "");
        }
        return null;
    }
}
