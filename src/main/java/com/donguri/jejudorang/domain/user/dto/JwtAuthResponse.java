package com.donguri.jejudorang.domain.user.dto;


import lombok.Builder;

import java.util.List;

@Builder
public record JwtAuthResponse (
        String token,
        String type,
        Long id,
        String externalId,
        List<String> roles
){}
