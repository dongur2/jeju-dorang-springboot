package com.donguri.jejudorang.domain.user.dto;

import com.donguri.jejudorang.domain.user.entity.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

@Builder
public record SignUpRequest (

        @Pattern(regexp=("^[a-zA-Z0-9]{5,20}$")
                ,message="아이디는 5자 이상 20자 이하의 영문자와 숫자 조합만 가능합니다.")
        @NotBlank(message = "아이디를 작성해주세요.")
        @Size(min = 5, max = 20, message = "아이디는 5자 이상 30자 이하만 가능합니다.")
        String externalId,

        @Pattern(regexp = ("^[a-zA-Z0-9가-힣]{2,15}$")
                ,message = "닉네임은 특수문자, 이모티콘을 제외한 2자 이상 15자 이하만 가능합니다.")
        @NotBlank(message = "닉네임을 작성해주세요.")
        @Size(min = 2, max = 15, message = "닉네임은 2자 이상 15자 이하만 가능합니다.")
        String nickname,

        @Email(message = "이메일 형식으로 작성해주세요.")
        @NotBlank(message = "이메일을 작성해주세요.")
        String email,

//        @NotBlank
        String agreement,

        @Pattern(regexp=("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$")
                ,message="비밀번호는 특수 문자와 숫자가 적어도 하나가 포함된 8자 이상 20자 이하만 가능합니다.")
        @Size(min = 8, max = 20)
        @NotBlank(message = "비밀번호를 작성해주세요.")
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
