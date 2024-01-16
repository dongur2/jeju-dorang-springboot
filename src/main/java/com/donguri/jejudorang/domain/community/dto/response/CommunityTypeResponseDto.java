package com.donguri.jejudorang.domain.community.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommunityTypeResponseDto {
    private final String typeForRedirect;

    @Builder
    public CommunityTypeResponseDto(String typeForRedirect) {
        this.typeForRedirect = typeForRedirect;
    }
}
