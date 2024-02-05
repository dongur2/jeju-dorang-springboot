package com.donguri.jejudorang.domain.user.service;


import com.donguri.jejudorang.domain.user.dto.request.*;
import com.donguri.jejudorang.domain.user.dto.request.email.MailChangeRequest;
import com.donguri.jejudorang.domain.user.dto.request.email.MailSendRequest;
import com.donguri.jejudorang.domain.user.dto.request.email.MailVerifyRequest;
import com.donguri.jejudorang.domain.user.dto.response.ProfileResponse;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Optional;

public interface UserService {

    // 이메일 인증번호 전송, 확인
    void sendVerifyMail(MailSendRequest mailSendRequest);
    boolean checkVerifyMail(MailVerifyRequest mailVerifyRequest);

    // 회원가입
    void signUp(SignUpRequest signUpRequest);

    // 로그인
    Map<String, String> signIn(LoginRequest loginRequest);

    // 로그아웃
    Optional<Authentication> logOut();


    // 프로필 정보 조회
    ProfileResponse getProfileData(String accessToken);


    // 프로필 사진, 닉네임, 이메일 수정
    void updateProfileData(String accessToken, ProfileRequest dataToUpdate) throws IllegalAccessException;

    // 프로필 사진 삭제
    void deleteProfileImg(String accessToken);

    // 비밀번호 수정
    void updatePassword(String accessToken, PasswordRequest pwdToUpdate) throws Exception;

    // 이메일 변경
    void updateEmail(String accessToken, MailChangeRequest emailToUpdate);
}
