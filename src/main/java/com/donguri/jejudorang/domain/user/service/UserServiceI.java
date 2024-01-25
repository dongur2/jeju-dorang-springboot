package com.donguri.jejudorang.domain.user.service;

import com.donguri.jejudorang.domain.user.dto.JwtAuthResponse;
import com.donguri.jejudorang.domain.user.dto.LoginRequest;
import com.donguri.jejudorang.domain.user.dto.SignUpRequest;
import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Password;
import com.donguri.jejudorang.domain.user.repository.RoleRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import com.donguri.jejudorang.global.config.JwtProvider;
import com.donguri.jejudorang.global.config.JwtUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceI implements UserService{

    @Autowired private final AuthenticationManager authenticationManager;
    @Autowired private final UserRepository userRepository;
    @Autowired private final RoleRepository roleRepository;
    @Autowired private final PasswordEncoder encoder;
    @Autowired private final JwtProvider jwtProvider;

    public UserServiceI(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, JwtProvider jwtProvider) {
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
    public JwtAuthResponse signIn(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.externalId(), loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider
                .generateTokenFromUserId(userRepository.findByExternalId(loginRequest.externalId())
                        .orElseThrow(() -> new RuntimeException("해당 아이디를 가진 유저가 없습니다.")).getId());

        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return JwtAuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(userDetails.getId())
                .externalId(userDetails.getProfile().getExternalId())
                .roles(roles)
                .build();
    }


}
