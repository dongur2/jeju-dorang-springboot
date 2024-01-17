package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.request.CommunityUpdateRequestDto;
import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityForModifyResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityTypeResponseDto;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.service.tag.CommunityWithTagService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class CommunityServiceI implements CommunityService {
    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityWithTagService communityWithTagService;

    @Override
    @Transactional
    public CommunityTypeResponseDto saveNewPost(CommunityWriteRequestDto postToWrite) {

        Community communityToWrite = postToWrite.toEntity();
        communityToWrite.setBoardType(postToWrite.type());
        communityToWrite.setDefaultJoinState();

        Community saved = communityRepository.save(communityToWrite);
        // 태그 제외 entity 생성 & 저장 완료

        // 태그 저장
        if (postToWrite.tags() != null) {
            communityWithTagService.saveTagToPost(saved, postToWrite.tags());
        }

        // 리다이렉트할 때 넣어줄 글타입
        String typeForDto = setTypeForRedirect(saved);
        return new CommunityTypeResponseDto(typeForDto);
    }

    @Override
    @Transactional
    public CommunityForModifyResponseDto getCommunityPost(Long communityId) {
        Community existingCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("다음 ID에 해당하는 글을 찾을 수 없습니다: " + communityId));
        existingCommunity.upViewCount();

        List<String> tagsToStringList = existingCommunity.getTags().stream().map(
                        communityWithTag -> communityWithTag.getTag().getKeyword())
                        .toList();

        return CommunityForModifyResponseDto.from(existingCommunity, tagsToStringList);
    }

    @Override
    @Transactional
    public CommunityTypeResponseDto updatePost(Long communityId, CommunityUpdateRequestDto postToUpdate) {

        Community existingCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("다음 ID에 해당하는 글을 찾을 수 없습니다: " + communityId));


        List<String> splitTagStringToUpdate;

        if (postToUpdate.tags().trim().isEmpty()) {
            splitTagStringToUpdate = null;
        } else {
            splitTagStringToUpdate = Arrays.stream(postToUpdate.tags().split(","))
                    .toList();
        }

        Community communityToUpdate = postToUpdate.toEntity();
        communityToUpdate.setBoardType(postToUpdate.type());
        communityToUpdate.setDefaultJoinState();
        communityRepository.save(communityToUpdate);
        // 태그 제외 업데이트

        if (postToUpdate.tags() != null) {
            communityWithTagService.saveTagToPost(communityToUpdate, postToUpdate.tags());
        }

        // 리다이렉트할 때 넣어줄 글타입
        String typeForDto = setTypeForRedirect(communityToUpdate);
        return new CommunityTypeResponseDto(typeForDto);
    }

    private static String setTypeForRedirect(Community resultCommunity) {
        if (resultCommunity.getType() == BoardType.PARTY) {
            return "parties";
        } else  {
            return "chats";
        }
    }


}
