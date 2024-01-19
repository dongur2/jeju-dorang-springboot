package com.donguri.jejudorang.domain.community.dto.request;


import com.donguri.jejudorang.domain.community.entity.Community;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record CommunityWriteRequestDto (

        @NotBlank(message = "제목은 비울 수 없습니다.")
        @Size(max = 60, message = "제목은 60자를 초과할 수 없습니다.")
        String title,

        @NotNull
        String type,

        @NotBlank(message = "글 내용은 비울 수 없습니다.")
        @Size(max = 4000, message = "길이 제한을 초과했습니다.")
        String content,

        String tags

) {
    public Community toEntity() {
        Community newEntity = Community.builder()
                .title(title)
                .content(content)
                .build();
        newEntity.setBoardType(type);
        newEntity.setDefaultJoinState();

        return newEntity;
    }
}
