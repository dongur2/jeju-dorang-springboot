package com.donguri.jejudorang.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record PasswordRequest (
        @NotBlank(message = "현재 비밀번호를 입력해주세요.")
        String oldPwd,

        @NotBlank(message = "새 비밀번호를 입력해주세요.")
        String newPwd,

        @NotBlank(message = "새 비밀번호를 한 번 더 입력해주세요.")
        String newPwdToCheck
){


}