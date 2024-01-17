package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.request.CommunityUpdateRequestDto;
import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityForModifyResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityTypeResponseDto;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
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

    @Override
    @Transactional
    public CommunityTypeResponseDto saveNewPost(CommunityWriteRequestDto post) {
        // 태그 리스트
        List<String> splitTagStringToWrite;

        // 태그 입력란에 아무것도 입력하지 않을 경우
        boolean isTagEmpty = post.getTags().trim().isEmpty();
        if (isTagEmpty) {
            splitTagStringToWrite = null;
        } else {
            splitTagStringToWrite = Arrays.stream(post.getTags().split(","))
                    .toList();
        }

        Community newPost = Community.builder()
                .title(post.getTitle())
                .tags(splitTagStringToWrite)
                .content(post.getContent())
                .build();
        newPost.setBoardType(post.getType());
        newPost.setDefaultJoinState();

        Community saved = communityRepository.save(newPost);

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

        return CommunityForModifyResponseDto.builder()
                .id(existingCommunity.getId())
                .type(existingCommunity.getType())
                .state(existingCommunity.getState())
                .title(existingCommunity.getTitle())
                .createdAt(existingCommunity.getCreatedAt())
                .updatedAt(existingCommunity.getUpdatedAt())
                .viewCount(existingCommunity.getViewCount())
                .content(existingCommunity.getContent())
                .tags(existingCommunity.getTags())
                .bookmarkCount(existingCommunity.getBookmarks().size())
                .build();
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
