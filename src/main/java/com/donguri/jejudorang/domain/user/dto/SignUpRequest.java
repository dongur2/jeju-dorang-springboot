package com.donguri.jejudorang.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public record SignUpRequest (

        @NotBlank
        @Size(min = 5, max = 30)
        String externalId,

        @NotBlank
        @Size(min = 2, max = 15)
        String nickname,

        @Email
        @NotBlank
        String email,

        @NotBlank
        String agreement,

        @NotBlank
        @Size(min = 6, max = 40)
        String password,

        @NotBlank
        String passwordForCheck,

        Set<String> role

){}
