package com.donguri.jejudorang.domain.user.service;


import com.donguri.jejudorang.domain.user.dto.SignUpRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> signUp(SignUpRequest signUpRequest);
}
