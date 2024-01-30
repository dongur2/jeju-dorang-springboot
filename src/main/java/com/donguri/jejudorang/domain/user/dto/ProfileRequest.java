package com.donguri.jejudorang.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record ProfileRequest (

        @Size(min = 2, max = 15, message = "닉네임은 최소 2자 이상 최대 15자 이하입니다.")
        @NotBlank(message = "닉네임은 비울 수 없습니다.")
        String nickname,

        @Email
        @Size(max = 50, message = "이메일은 최대 50자를 넘을 수 없습니다.")
        @NotBlank(message = "이메일은 비울 수 없습니다.")
        String email,

        MultipartFile img

){}
