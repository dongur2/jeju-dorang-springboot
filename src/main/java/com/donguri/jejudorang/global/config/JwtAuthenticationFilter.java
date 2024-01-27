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

    @Value("${jwt.cookie-expire}")
    private int cookieTime;

    @Autowired private JwtProvider jwtProvider;
    @Autowired private JwtUserDetailsService jwtUserDetailsService;
    @Autowired private RefreshTokenRepository refreshTokenRepository;


    /*
    * Request Cookie의 유효 토큰 필터링
    * > 타임리프는 서버사이드 렌더링 -> header 토큰 전달 곤란
    * > 임시로 쿠키에 토큰 전달
    *
    * doFilterInternal은 Request(요청)이 발생할 때마다 컨트롤러 -> JwtAuthFilter.doFilterInternal -> doFilter -> ... -> Service 로 프로세스
    * => 요청이 발생할 때마다 요청 비즈니스 로직 처리 전에 쿠키의 토큰을 확인해 인증 정보를 확인
    *
    * */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("doFilterInternal: 인증/권한 검증 프로세스 시작");

        /*
        * 쿠키의 Access Token 검증
        * */
        String accessToken = getJwtFromRequest(request, "access_token"); // Cookie에 같이 가지고 있던 Access Token 추출
        log.info("쿠키에서 Access Token 추출 완료 : {}", accessToken);

        try {
            if (accessToken != null && jwtProvider.validateJwtToken(accessToken)) {
                String username = jwtProvider.getUserNameFromJwtToken(accessToken); // 토큰에서 externalId 추출
                log.info("현재 인증 대상 유저: {}", username);

                setContextAuthWithToken(request, username, accessToken); // SecurityContext에 인증 정보 설정
            }

        } catch (IllegalArgumentException e) {
            log.error("아이디/비밀번호가 틀립니다: {}", e.getMessage());
            SecurityContextHolder.clearContext();

        } catch (ExpiredJwtException e) {
            log.error("토큰이 만료되었습니다 : {}", e.getMessage()); // access token 만료

            /*
            * Access Token이 만료되었을 경우 Cookie에 같이 가지고 있던 Refresh Token을 가져와 인증하고 Access Token 재발급
            * */
            String refreshToken = getJwtFromRequest(request, "refresh_token"); // Cookie에 같이 가지고 있던 Refresh Token 추출
            log.info("쿠키에서 Refresh Token 추출 완료 : {}", refreshToken);

            try {
                if (refreshToken != null && jwtProvider.validateJwtToken(refreshToken)) {
                    String username = jwtProvider.getUserNameFromJwtToken(refreshToken); // 토큰에서 externalId 추출
                        log.info("현재 Access Token이 만료되어 재발급 & 인증을 시도하는 유저 [Refresh Token]: {}", username);

                    String refreshRepo = getRefreshTokenFromRedis(refreshToken, username); // Refresh Token from Redis

                    /*
                    * DB에서 가져온 Refresh Token과 Cookie에서 가져온 Refresh Token이 같을 경우에 재발급 진행
                    * */
                    if (refreshRepo.equals(refreshToken)) {
                        UsernamePasswordAuthenticationToken authentication = setContextAuthWithToken(request, username, refreshToken);
                        String newAccess = jwtProvider.generateAccessToken(authentication);

                        createCookieForUpdateToken("access_token", newAccess, response);
                        log.info("Access Token 재발급 & Cookie access_token 갱신 완료");

                        /*
                        * Refresh Token 만료 3분 전부터 Refresh Token 갱신
                        * */
                        if (jwtProvider.getTokenExpirationFromJWT(refreshToken)
                                .before(new Date(System.currentTimeMillis() + 60 * 3000))) {

                            String newRefresh = jwtProvider.generateRefreshTokenFromUserId(authentication);

                            // Redis에 존재하던 이전 토큰 삭제 후 새로운 토큰 저장
                            refreshTokenRepository.deleteById(refreshToken);
                            refreshTokenRepository.save(RefreshToken.builder()
                                    .refreshToken(newRefresh)
                                    .userId(username)
                                    .build()
                            );
                            log.info("Refresh Token 재발급 완료 : {} -> {}", refreshToken, newRefresh);

                            createCookieForUpdateToken("refresh_token", newRefresh, response);
                            log.info("Refresh Token Cookie 갱신 완료");
                        }

                    } else {
                        log.error("Refresh Token이 일치하지 않습니다 : cookie = {} and repo = {}", refreshToken, refreshRepo);
                    }
                }

            } catch (ExpiredJwtException exx) {
                log.error("Refresh Token이 만료되었습니다 : {}", refreshToken);
                SecurityContextHolder.clearContext();

            } catch (Exception ex) {
                log.error("Refresh Token Exception : {}", ex.getMessage());
            }

        } catch (Exception e) {
            log.error("Security Context 유저 인증에 실패했습니다 : {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /*
    * Redis에서 RefreshToken 가져오기
    * */
    private String getRefreshTokenFromRedis(String refreshToken, String username) {
        return refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new RuntimeException("DB에 해당 유저의 유효한 Refresh Token이 없습니다 : " + username))
                .getRefreshToken();
    }

    /*
    * 재발급한 토큰을 Cookie에 갱신
    * */
    private void createCookieForUpdateToken(String cookieName, String regeneratedToken, HttpServletResponse response) {
        Cookie cookieToUpdate = new Cookie(cookieName, regeneratedToken);
        cookieToUpdate.setHttpOnly(true);
        cookieToUpdate.setMaxAge(cookieTime);
        cookieToUpdate.setPath("/");

        response.addCookie(cookieToUpdate);
    }

    private UsernamePasswordAuthenticationToken setContextAuthWithToken(HttpServletRequest request, String username, String accessToken) {
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username); // externalId 기반 UserDetails(사용자 상세 정보 - 권한+a) 로드
        List<GrantedAuthority> authorities = jwtProvider.getAuthoritiesFromJWT(accessToken); // 토큰에서 사용자 권한 추출
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities); // 인증 토큰 생성

        // 인증된 사용자 설정
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Security Context에 인증 정보 설정을 완료했습니다 : {}", authentication);

        return authentication;
    }

    /*
    * Cookie에서 토큰 추출
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
