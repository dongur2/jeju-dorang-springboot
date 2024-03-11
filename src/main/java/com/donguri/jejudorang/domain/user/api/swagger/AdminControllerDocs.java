package com.donguri.jejudorang.domain.user.api.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin", description = "관리자 관련 API")
public interface AdminControllerDocs {

    @Operation(summary = "관리자 회원가입 폼 화면 출력")
    public String adminRegisterForm();
}
