package com.donguri.jejudorang.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record ProfileRequest (

        @Size(min = 2, max = 15, message = "닉네임은 최소 2자 이상 최대 15자 이하입니다.")
        @NotBlank(message = "닉네임을 입력해주세요.")
        String nickname,

        MultipartFile img

){}
