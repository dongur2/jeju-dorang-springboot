package com.donguri.jejudorang.domain.user.dto.request.email;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "이메일 변경 요청 정보")
public record MailChangeRequest (

        @Schema(description = "변경할 이메일", example = "testuser2@mail.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @Email(message = "이메일 형식으로 작성해주세요.")
        @NotBlank(message = "이메일을 작성해주세요.")
        String emailToSend,

        @Schema(description = "이메일 인증 여부; false일 경우 이메일 변경이 불가능합니다.", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        @AssertTrue(message = "이메일 인증이 필요합니다.")
        boolean isVerified

){}