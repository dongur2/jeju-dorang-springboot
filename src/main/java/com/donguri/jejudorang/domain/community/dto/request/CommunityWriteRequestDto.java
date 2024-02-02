package com.donguri.jejudorang.domain.community.dto.request;


import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;


@Builder
public record CommunityWriteRequestDto (

        @NotBlank(message = "제목을 작성해주세요.")
        @Size(min= 2, max = 60, message = "제목은 2자 이상 60자 이하만 가능합니다.")
        String title,

        @NotBlank(message = "Error")
        String type,

        @NotBlank(message = "글 내용을 작성해주세요.")
        @Size(min= 2, max = 4000, message = "글 내용은 2자 이상 4000자 이하만 가능합니다.")
        String content,

        String tags

) {
    public Community toEntity(User writer) {
        Community newEntity = Community.builder()
                .title(title)
                .writer(writer)
                .content(content)
                .build();
        newEntity.setBoardType(type);
        newEntity.setDefaultJoinState();

        return newEntity;
    }
}
