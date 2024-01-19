package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityForModifyResponseDto;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;


@SpringBootTest
class CommunityServiceITest {
    @Autowired
    CommunityRepository communityRepository;
    @Autowired
    CommunityService communityService;

    @Test
    void DTO_NotBlank_테스트() {
        // given
        CommunityWriteRequestDto writeDto =
                new CommunityWriteRequestDto("", "parties", "내용5", "일이삼사오육칠팔구십");
        // when
        try {
            communityService.saveNewPost(writeDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // then
    }

    @Test
    void 글작성_태그_길이_테스트() {

        // given
        CommunityWriteRequestDto writeDto =
                new CommunityWriteRequestDto("제목5", "parties", "내용5", "일이삼사오육칠팔구십일이삼사오육칠팔구십");
        // when
        communityService.saveNewPost(writeDto);

        // then
        CommunityForModifyResponseDto savedDto = communityService.getCommunityPost(6L);
        Assertions.assertThat(savedDto.title()).isEqualTo(writeDto.title());

        savedDto.tags().forEach(tag -> System.out.println("tag = " + tag));

    }
    @Test
    void 글작성_태그_길이_예외_처리_테스트() {

        // given
        CommunityWriteRequestDto writeDto =
                new CommunityWriteRequestDto("제목5", "parties", "내용5", "일이삼사오육칠팔구십일이삼사오육칠팔구십뿅");
        // when
        try {
            communityService.saveNewPost(writeDto);
            // then
            CommunityForModifyResponseDto savedDto = communityService.getCommunityPost(6L);
            Assertions.assertThat(savedDto.title()).isEqualTo(writeDto.title());

            savedDto.tags().forEach(tag -> System.out.println("tag = " + tag));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}