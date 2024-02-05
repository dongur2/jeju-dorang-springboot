package com.donguri.jejudorang.domain.user.dto.request.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record MailVerifyForPwdRequest(

        @Email(message = "이메일을 입력해주세요.")
        @NotBlank(message = "이메일을 입력해주세요.")
        String email,

        @NotBlank(message = "아이디를 입력해주세요.")
        String externalId,

        @NotBlank(message = "인증번호를 입력해주세요")
        String code

){}