package com.donguri.jejudorang.domain.user.service;

import com.donguri.jejudorang.domain.user.dto.LoginRequest;
import com.donguri.jejudorang.domain.user.dto.ProfileRequest;
import com.donguri.jejudorang.domain.user.dto.ProfileResponse;
import com.donguri.jejudorang.domain.user.dto.SignUpRequest;
import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Password;
import com.donguri.jejudorang.domain.user.repository.RoleRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import com.donguri.jejudorang.domain.user.service.s3.ImageService;
import com.donguri.jejudorang.global.config.JwtProvider;
import com.donguri.jejudorang.global.config.JwtUserDetails;
import com.donguri.jejudorang.global.config.RefreshToken;
import com.donguri.jejudorang.global.config.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class UserServiceI implements UserService {

    @Autowired
    private ImageService imageService;

    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final PasswordEncoder encoder;
    @Autowired
    private final JwtProvider jwtProvider;

    public UserServiceI(RefreshTokenRepository refreshTokenRepository, AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, JwtProvider jwtProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtProvider = jwtProvider;
    }


    @Override
    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        if (userRepository.findByExternalId(signUpRequest.externalId()).isPresent()) {
            throw new RuntimeException("Error: 이미 존재하는 아이디입니다.");
        }

        if (userRepository.findByEmail(signUpRequest.email()).isPresent()) {
            throw new RuntimeException("Error: 이미 존재하는 이메일입니다.");
        }

        if (!signUpRequest.password().equals(signUpRequest.passwordForCheck())) {
            throw new RuntimeException("Error: 비밀번호가 일치하지 않습니다.");
        }

        /*
         * Create a new User
         * */
        User userToSave = signUpRequest.toEntity();

        // set password
        Password pwdToSet = Password.builder()
                .user(userToSave)
                .build();
        pwdToSet.updatePassword(encoder, signUpRequest.password()); // encode pwd
        userToSave.updatePwd(pwdToSet);

        // set role
        Set<String> strRoles = signUpRequest.role();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.USER)
                    .orElseThrow(() -> new RuntimeException("Error: 권한을 찾을 수 없습니다"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if (role.equals("admin")) {
                    Role adminRole = roleRepository.findByName(ERole.ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: 권한을 찾을 수 없습니다."));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName(ERole.USER)
                            .orElseThrow(() -> new RuntimeException("Error: 권한을 찾을 수 없습니다."));
                    roles.add(userRole);
                }
            });
        }

        userToSave.updateRole(roles);

        userRepository.save(userToSave);
    }


    @Override
    @Transactional
    public Map<String, String> signIn(LoginRequest loginRequest) {
        log.info("[User Service] SIGN IN START ** ");

        try {
            // 로그인 아이디, 비밀번호 기반으로 유저 정보(JwtUserDetails) 찾아서 Authentication 리턴
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.externalId(), loginRequest.password()));
            log.info("[SignIn Service] 현재 로그인한 유저 인증 : {}", String.valueOf(authentication));

            // securityContext에 authentication 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("[SignIn Service] Security Context에 인증 정보 설정 : {}", String.valueOf(authentication));

            // 인증한 정보 기반으로 access token 생성
            String jwtAccess = jwtProvider.generateAccessToken(authentication);
            log.info("[SignIn Service] Access Token 생성 완료 : {}", jwtAccess);

            // refresh token
            String jwtRefresh = null;
            Optional<RefreshToken> refreshOp = refreshTokenRepository.findByUserId(authentication.getName());
            if (refreshOp.isPresent()) {
                log.info("Redis에 해당 아이디의 유효한 Refresh Token이 존재: {}", refreshOp);
                jwtRefresh = refreshOp.get().getRefreshToken();

            } else { // refresh token은 만료기간에 맞춰 redis에서 자동 삭제
                log.info("Redis에 해당 아이디의 Refresh Token이 없음: {}", authentication.getName());

                /*
                 * Refresh Token 생성 후 Redis에 저장
                 * */
                jwtRefresh = jwtProvider.generateRefreshTokenFromUserId(authentication);
                RefreshToken refreshTokenToSave = RefreshToken.builder()
                        .refreshToken(jwtRefresh)
                        .userId(authentication.getName())
                        .build();

                RefreshToken saved = refreshTokenRepository.save(refreshTokenToSave);
                log.info("Redis에 Refresh Token이 저장되었습니다 : {}", saved.getRefreshToken());
            }

            // 인증된 정보 기반 해당 사용자 세부 정보
            JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
            log.info("인증 정보 기반 사용자 세부 정보 : {}", userDetails.getUsername());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", jwtAccess);
            tokens.put("refresh_token", jwtRefresh);

            return tokens;

        } catch (BadCredentialsException e) {
            log.error("아이디/비밀번호가 올바르지 않습니다 : {}", e.getMessage());
            return null;

        } catch (Exception e) {
            log.error("인증에 실패했습니다 : {}", e.getMessage());
            return null;
        }

    }

    /*
     * SecurityConfig .logout() 설정으로 실행되지 않음
     * */
    @Override
    @Transactional
    public Optional<Authentication> logOut() {
        SecurityContextHolder.clearContext();
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    @Override
    @Transactional
    public ProfileResponse getProfileData(String token) {
        try {
            User nowUser = getNowUser(token);
            return ProfileResponse.builder().build()
                    .from(nowUser);

        } catch (Exception e) {
            log.error("프로필 조회에 실패했습니다 : {}", e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public void updateProfileData(String token, ProfileRequest dataToUpdate) {
        try {
            User nowUser = getNowUser(token);

            if (!dataToUpdate.img().isEmpty()) {
                imageService.putS3Object(dataToUpdate.img());
            }
            log.info("이미지 업로드 완료");

            Profile profile = nowUser.getProfile();
            profile.updateNickname(dataToUpdate.nickname());

            nowUser.getAuth().updateEmail(dataToUpdate.email()); // 추가 인증 필요

        } catch (Exception e) {
            log.error("프로필 업데이트에 실패했습니다 : {}", e.getMessage());
        }
    }

    private User getNowUser(String token) {
        String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(token);

        return userRepository.findByExternalId(userNameFromJwtToken)
                .orElseThrow(() -> new RuntimeException("아이디에 해당하는 유저가 없습니다: " + userNameFromJwtToken));
    }

}
