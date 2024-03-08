package com.donguri.jejudorang.domain.community.dto.request;


import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;


@Builder
@Schema(name = "커뮤니티 글 작성 요청 정보")
public record CommunityWriteRequest(

        @Schema(description = "2자 이상 60자 이하로 이루어진 제목 (공백문자/null 비허용)", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "제목을 작성해주세요.")
        @Size(min= 2, max = 60, message = "제목은 2자 이상 60자 이하만 가능합니다.")
        String title,

        @Schema(description = "게시판 분류(게시글 타입)", example = "party/chat", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "[오류] 글 타입이 필요합니다.")
        String type,

        @Schema(description = "4000자 이하로 이루어진 내용 (공백문자/null 비허용)", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "글 내용을 작성해주세요.")
        @Size(max = 4000, message = "글 내용은 4000자 이하만 가능합니다.")
        String content,

        @Schema(description = "태그 목록", example = "태그1,태그2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
