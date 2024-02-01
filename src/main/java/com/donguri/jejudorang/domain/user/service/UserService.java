package com.donguri.jejudorang.domain.user.service;


import com.donguri.jejudorang.domain.user.dto.*;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Optional;

public interface UserService {

    void sendVerifyMail(MailSendRequest mailSendRequest);
    boolean checkVerifyMail(MailVerifyRequest mailVerifyRequest);
    void signUp(SignUpRequest signUpRequest);

    Map<String, String> signIn(LoginRequest loginRequest);

    Optional<Authentication> logOut();

    ProfileResponse getProfileData(String accessToken);

    ProfileResponse updateProfileData(String accessToken, ProfileRequest dataToUpdate);
    ProfileResponse updateProfileData(String accessToken);
}
