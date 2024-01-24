package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.dto.MessageResponse;
import com.donguri.jejudorang.domain.user.dto.SignUpRequest;
import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.repository.RoleRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import com.donguri.jejudorang.global.config.JwtProvider;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Controller
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;

    @GetMapping("/signup")
    public String registerForm(Model model) {
        model.addAttribute("errorMsg", "SIGN UP FORM");
        return "/error/errorTemp";
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        log.info("success");

        if (userRepository.findByExternalId(signUpRequest.externalId()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: 이미 존재하는 아이디입니다."));
        }

        if (userRepository.findByEmail(signUpRequest.email()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: 이미 존재하는 이메일입니다."));
        }

        if (signUpRequest.password().equals(signUpRequest.passwordForCheck())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: 비밀번호가 일치하지 않습니다."));
        }

        /*
        * Create a new User
        * */
        User user = User.builder()
                .loginType(LoginType.BASIC)
                .profile(Profile.builder()
                        .externalId(signUpRequest.externalId())
                        .nickname(signUpRequest.nickname())
                        .build())
                .auth(Authentication.builder()
                        .email(signUpRequest.email())
                        .agreement(AgreeRange.ALL)
                        .build())
                .build();

        Set<String> strRoles = signUpRequest.role();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if (role.equals("admin")) {
                    Role adminRole = roleRepository.findByName(ERole.ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName(ERole.USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                }
            });
        }

        user.updateRole(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully"));

    }
}
