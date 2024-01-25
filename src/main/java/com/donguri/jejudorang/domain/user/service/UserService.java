package com.donguri.jejudorang.domain.user.service;


import com.donguri.jejudorang.domain.user.dto.JwtAuthResponse;
import com.donguri.jejudorang.domain.user.dto.LoginRequest;
import com.donguri.jejudorang.domain.user.dto.SignUpRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
    void signUp(SignUpRequest signUpRequest);

    ResponseEntity<?> signIn(LoginRequest loginRequest);
}
