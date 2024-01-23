package com.donguri.jejudorang.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest (

    @NotBlank
    String externalId,

    @NotBlank
    String password

){}
