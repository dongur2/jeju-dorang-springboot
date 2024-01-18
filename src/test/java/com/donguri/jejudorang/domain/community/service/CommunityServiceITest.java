package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityForModifyResponseDto;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class CommunityServiceITest {
    @Autowired
    CommunityRepository communityRepository;
    @Autowired
    CommunityService communityService;

    @Test
    void 글작성_태그_길이_테스트() {

        // given
        CommunityWriteRequestDto writeDto =
                new CommunityWriteRequestDto("제목5", "parties", "내용5", "태그,테스트,다섯번째");
        // when
        communityService.saveNewPost(writeDto);

        // then
        CommunityForModifyResponseDto savedDto = communityService.getCommunityPost(6L);
        Assertions.assertThat(savedDto.title()).isEqualTo(writeDto.title());

        savedDto.tags().forEach(tag -> System.out.println("tag = " + tag));

    }

}