package com.donguri.jejudorang.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "비밀번호 변경 요청 정보")
public record PasswordRequest (
        @Schema(description = "현재 비밀번호", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "현재 비밀번호를 입력해주세요.")
        String oldPwd,

        @Schema(description = "새 비밀번호 (8자 이상 20자 이하의 특수문자와 숫자가 적어도 하나가 포함된 문자열)", requiredMode = Schema.RequiredMode.REQUIRED)
        @Pattern(regexp=("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$")
                ,message="비밀번호는 특수 문자와 숫자가 적어도 하나가 포함된 8자 이상 20자 이하만 가능합니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하만 가능합니다.")
        @NotBlank(message = "새 비밀번호를 입력해주세요.")
        String newPwd,

        @Schema(description = "새 비밀번호 확인", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "새 비밀번호를 한 번 더 입력해주세요.")
        String newPwdToCheck
){}