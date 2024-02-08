package com.donguri.jejudorang.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest (

    @NotBlank(message = "아이디를 입력해주세요.")
    String externalId,

    @NotBlank(message = "비밀번호를 입력해주세요.")
    String password

){}
