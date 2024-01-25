package com.donguri.jejudorang.domain.user.dto;

import com.donguri.jejudorang.domain.user.entity.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record SignUpRequest (

        @NotBlank(message = "아이디는 비울 수 없습니다.")
        @Size(min = 5, max = 30, message = "아이디는 5자 이상 30자 이하만 가능합니다.")
        String externalId,

        @NotBlank(message = "닉네임은 비울 수 없습니다.")
        @Size(min = 2, max = 15, message = "닉네임은 2자 이상 15자 이하만 가능합니다.")
        String nickname,

        @Email
        @NotBlank(message = "이메일은 비울 수 없습니다.")
        String email,

//        @NotBlank
        String agreement,

        @NotBlank(message = "비밀번호는 비울 수 없습니다.")
        @Size(min = 6, max = 40)
        String password,

        @NotBlank(message = "비밀번호를 한 번 더 입력해주세요.")
        String passwordForCheck,

        Set<String> role

){
        public User toEntity() {
                User user = User.builder()
                        .loginType(LoginType.BASIC) // temp
                        .build();

                Profile prof = Profile.builder()
                        .user(user)
                        .externalId(externalId)
                        .nickname(nickname)
                        .build();

                Authentication auth = Authentication.builder()
                        .user(user)
                        .agreement(AgreeRange.ALL) // temp
                        .email(email)
                        .build();

                user.updateProfile(prof);
                user.updateAuth(auth);

                return user;
        }
}
