package com.donguri.jejudorang.domain.user.service;

import com.donguri.jejudorang.domain.user.dto.LoginRequest;
import com.donguri.jejudorang.domain.user.dto.SignUpRequest;
import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Password;
import com.donguri.jejudorang.domain.user.repository.RoleRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import com.donguri.jejudorang.global.config.JwtProvider;
import com.donguri.jejudorang.global.config.JwtUserDetails;
import com.donguri.jejudorang.global.config.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class UserServiceI implements UserService{

    @Value("${jwt.expiration-time.refresh}")
    private long expire_time;

    @Autowired private final RefreshTokenRepository refreshTokenRepository;

    @Autowired private final AuthenticationManager authenticationManager;

    @Autowired private final UserRepository userRepository;
    @Autowired private final RoleRepository roleRepository;

    @Autowired private final PasswordEncoder encoder;

    @Autowired private final JwtProvider jwtProvider;

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
    public String signIn(LoginRequest loginRequest) {
        log.info("USER SERVICE ======== Sign IN");

        try {
            // 로그인 아이디, 비밀번호 기반으로 유저 정보(JwtUserDetails) 찾아서 Authentication 리턴
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.externalId(), loginRequest.password()));
            log.info("SERVICE AUTHENTICATION 인증 === {}",String.valueOf(authentication));

            // securityContext에 authentication 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("SERVICE AUTHENTICATION 인증 CONTEXT 설정 === {}",String.valueOf(authentication));

            // 인증한 정보 기반으로 access token 생성
            String jwtAccess = jwtProvider.generateAccessToken(authentication);
            log.info("JWT ACCESS TOKEN 생성 ============ {}",jwtAccess);

            // 인증된 정보 기반 해당 사용자 세부 정보
            JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
            log.info("인증 정보 기반 사용자 세부 정보 ============ {}",userDetails.getUsername());

            return jwtAccess;

        } catch (Exception e) {
            log.error("인증 실패==========={}", e.getMessage());
            return null;
        }

        // redis 저장
//        if (refreshTokenRepository.findByUserId(userDetails.getProfile().getId()).isEmpty()){
//            log.info("refreshTokenRepository Redis saved");
//            // 인증한 정보 기반으로 refresh token 생성
//            String jwtRefresh = jwtProvider.generateRefreshTokenFromUserId(userRepository.findByExternalId(loginRequest.externalId())
//                    .orElseThrow(() -> new RuntimeException("해당 아이디를 가진 유저가 없습니다.")).getId());
//
//            refreshTokenRepository.save(RefreshToken.builder()
//                    .refreshToken(jwtRefresh)
//                    .userId(userDetails.getProfile().getId())
//                    .authorities(userDetails.getAuthorities())
//                    .build());
//        }
        }
        @Override
        @Transactional
        public Optional<Authentication> logOut() {
            log.info("LOGOUT SERVICE IN ========= ");
            SecurityContextHolder.clearContext(); // securityContext의 인증정보 제거

            return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
        }
}
