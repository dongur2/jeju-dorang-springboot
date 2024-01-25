package com.donguri.jejudorang.domain.user.service;


import com.donguri.jejudorang.domain.user.dto.JwtAuthResponse;
import com.donguri.jejudorang.domain.user.dto.LoginRequest;
import com.donguri.jejudorang.domain.user.dto.SignUpRequest;

public interface UserService {
    void signUp(SignUpRequest signUpRequest);

    JwtAuthResponse signIn(LoginRequest loginRequest);
}
