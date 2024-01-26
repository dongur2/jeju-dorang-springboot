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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
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

        String accessToken = getJwtFromRequest(request, "access_token"); // 쿠키에서 추출한 토큰
        log.info("요청 쿠키에서 추출한 액세스토큰 ==== {}", accessToken);

        try {

            if (accessToken != null && jwtProvider.validateJwtToken(accessToken)) {
                String username = jwtProvider.getUserNameFromJwtToken(accessToken); // 토큰에서 externalId 추출
                log.info("Authentication User ---- {}", username);

                UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username); // externalId 기반 UserDetails(사용자 상세 정보 - 권한+a) 로드
                List<GrantedAuthority> authorities = jwtProvider.getAuthoritiesFromJWT(accessToken); // 토큰에서 사용자 권한 추출
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities); // 인증 토큰 생성

                // 인증된 사용자 설정
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("인증 설정 완료 ----- {}", authentication);
            }

        } catch (IllegalArgumentException e) {
            log.error("아이디/비밀번호가 틀립니다: Security Context 유저 인증 실패 {}", e.getMessage());
            SecurityContextHolder.clearContext();

        } catch (ExpiredJwtException e) {
            log.error("토큰이 만료되었습니다 {}", e.getMessage()); // access token

            String refreshToken = getJwtFromRequest(request, "refresh_token"); // 쿠키에서 추출한 토큰
            log.info("요청 쿠키에서 추출한 리프레쉬토큰 ==== {}", refreshToken);

            try {

                // refresh token으로 재인증 후 service단으로 가서 access token 새로 발급
                if (refreshToken != null) {
                    String username = jwtProvider.getUserNameFromJwtToken(refreshToken); // 토큰에서 externalId 추출
                    log.info("REFRESH TOKEN USERNAME ========= {}", username);

                    log.info("RTR TOKEN {} ",refreshTokenRepository.findById(refreshToken).get().getRefreshToken());
                    // redis refresh token
                    String refreshRepo = refreshTokenRepository.findById(refreshToken)
                            .orElseThrow(() -> new RuntimeException("refresh token이 없습니다 : " + username))
                            .getRefreshToken();

                    if (refreshRepo.equals(refreshToken)) {
                        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username); // externalId 기반 UserDetails(사용자 상세 정보 - 권한+a) 로드
                        List<GrantedAuthority> authorities = jwtProvider.getAuthoritiesFromJWT(refreshToken); // 토큰에서 사용자 권한 추출
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities); // 인증 토큰 생성

                        // 인증된 사용자 설정
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("인증 설정 완료 ----- {}", authentication);

                        // 만료 3분 전 refresh token 갱신
                        if (jwtProvider.getTokenExpirationFromJWT(refreshToken)
                                .before(new Date(System.currentTimeMillis() + 60 * 3000))) {

                            String newRefresh = jwtProvider.generateRefreshTokenFromUserId(authentication);

                            refreshTokenRepository.deleteById(refreshToken);
                            refreshTokenRepository.save(RefreshToken.builder()
                                            .refreshToken(newRefresh)
                                            .userId(username)
                                            .build()
                            );
                            log.info("REFRESH TOKEN 갱신 ========= {} -> {}", refreshToken, newRefresh);
                        }

                    } else {
                        log.error("Refresh Token이 일치하지 않습니다 : cookie = {} and repo = {}", refreshToken, refreshRepo);
                    }
                }

            } catch (Exception ex) {
                log.error("refresh Exception : {}", ex.getMessage());
                log.error("refresh Token 만료: {}", refreshToken);
                SecurityContextHolder.clearContext();

            }

        } catch (Exception e) {
            log.error("Security Context 유저 인증에 실패: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /*
    * 쿠키에서 토큰 추출
    * */
    private String getJwtFromRequest(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(name)) {
                    return c.getValue();
                }
            }
        }
        return null;
    }
}
