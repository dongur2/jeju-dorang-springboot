package com.donguri.jejudorang.domain.user.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MailChangeRequest (

        @Email(message = "이메일 형식으로 작성해주세요.")
        @NotBlank(message = "이메일을 작성해주세요.")
        String emailToSend,

        @AssertTrue(message = "이메일 인증이 필요합니다.")
        boolean isVerified

){}