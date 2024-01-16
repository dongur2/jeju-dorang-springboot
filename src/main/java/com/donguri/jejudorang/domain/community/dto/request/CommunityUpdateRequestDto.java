package com.donguri.jejudorang.domain.community.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityUpdateRequestDto {
    private Long communityId;
    private String title;
    private String tags;
    private String type;
    private String content;
}
