package com.donguri.jejudorang.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 정보")
public record LoginRequest (

    @Schema(description = "아이디", example = "testuser1", required = true)
    @NotBlank(message = "아이디를 입력해주세요.")
    String externalId,

    @Schema(description = "비밀번호", example = "testuser~11111", required = true)
    @NotBlank(message = "비밀번호를 입력해주세요.")
    String password

){}
