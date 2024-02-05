package com.donguri.jejudorang.domain.user.service;


import com.donguri.jejudorang.domain.user.dto.*;
import org.apache.coyote.BadRequestException;
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


    // 프로필 사진, 닉네임, 이메일 수정
    ProfileResponse updateProfileData(String accessToken, ProfileRequest dataToUpdate);
    // 프로필 사진 삭제
    void deleteProfileImg(String accessToken);

    // 비밀번호 수정
    void updatePassword(String accessToken, PasswordRequest pwdToUpdate) throws BadRequestException;
}
