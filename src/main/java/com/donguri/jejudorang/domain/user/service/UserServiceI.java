package com.donguri.jejudorang.domain.user.service;

import com.donguri.jejudorang.domain.user.dto.LoginRequest;
import com.donguri.jejudorang.domain.user.dto.SignUpRequest;
import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Password;
import com.donguri.jejudorang.domain.user.repository.RoleRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import com.donguri.jejudorang.global.config.JwtProvider;
import com.donguri.jejudorang.global.config.JwtUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserServiceI implements UserService{

    @Value("${jwt.expiration-time.refresh}")
    private long expire_time;

    @Autowired private final RedisTemplate<String, String> redisTemplate;

    @Autowired private final AuthenticationManager authenticationManager;

    @Autowired private final UserRepository userRepository;
    @Autowired private final RoleRepository roleRepository;

    @Autowired private final PasswordEncoder encoder;

    @Autowired private final JwtProvider jwtProvider;

    public UserServiceI(RedisTemplate<String, String> redisTemplate, AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, JwtProvider jwtProvider) {
        this.redisTemplate = redisTemplate;
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
    public ResponseEntity<?> signIn(LoginRequest loginRequest) {

        // 로그인 아이디, 비밀번호 기반으로 AuthenticationToken 생성해서 인증 수행(authenticate)한 뒤, 성공하면 Authentication 생성
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.externalId(), loginRequest.password()));

        // 인증한 정보 기반으로 토큰 생성
        String jwtAccess = jwtProvider.generateTokenFromUserId(userRepository.findByExternalId(loginRequest.externalId())
                        .orElseThrow(() -> new RuntimeException("해당 아이디를 가진 유저가 없습니다.")).getId());

        // 인증한 정보 기반으로 refresh token 생성
        String jwtRefresh = jwtProvider.generateRefreshTokenFromUserId(userRepository.findByExternalId(loginRequest.externalId())
                .orElseThrow(() -> new RuntimeException("해당 아이디를 가진 유저가 없습니다.")).getId());

        // 인증된 정보 기반 해당 사용자 세부 정보
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();

        // Redis 저장
        redisTemplate.opsForValue().set(
                userDetails.getProfile().getExternalId(), // key
                jwtRefresh, // value
                expire_time, // timeout
                TimeUnit.MILLISECONDS
        );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwtAccess);

        return new ResponseEntity<>(jwtAccess, httpHeaders, HttpStatus.OK);
    }


}
