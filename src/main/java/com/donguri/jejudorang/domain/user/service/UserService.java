package com.donguri.jejudorang.domain.user.service;


import com.donguri.jejudorang.domain.user.dto.LoginRequest;
import com.donguri.jejudorang.domain.user.dto.ProfileRequest;
import com.donguri.jejudorang.domain.user.dto.ProfileResponse;
import com.donguri.jejudorang.domain.user.dto.SignUpRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Optional;

public interface UserService {
    void signUp(SignUpRequest signUpRequest);

    Map<String, String> signIn(LoginRequest loginRequest);

    Optional<Authentication> logOut();

    ProfileResponse getProfileData(String accessToken);

    ProfileResponse updateProfileData(String accessToken, ProfileRequest dataToUpdate);
}
