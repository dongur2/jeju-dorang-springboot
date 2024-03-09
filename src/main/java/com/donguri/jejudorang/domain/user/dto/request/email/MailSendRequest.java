package com.donguri.jejudorang.domain.user.dto.request.email;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(name = "이메일 전송 요청 정보")
public record MailSendRequest(
   @Email
   @NotBlank(message = "이메일을 입력해주세요")
   String email
){}
