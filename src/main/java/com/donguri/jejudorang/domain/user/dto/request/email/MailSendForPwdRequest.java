package com.donguri.jejudorang.domain.user.dto.request.email;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(name = "비밀번호 찾기 이메일 요청 정보")
public record MailSendForPwdRequest (
        @Email(message = "이메일을 입력해주세요.")
        @NotBlank(message = "이메일을 입력해주세요.")
        String email,

        @NotBlank(message = "아이디를 입력해주세요.")
        String externalId
){}