package com.donguri.jejudorang.domain.user.service;


import com.donguri.jejudorang.domain.user.dto.SignUpRequest;

public interface UserService {
    void signUp(SignUpRequest signUpRequest);
}
