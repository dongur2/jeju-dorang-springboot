package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.request.CommunityUpdateRequestDto;
import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityForModifyResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityTypeResponseDto;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
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

        return CommunityTypeResponseDto.builder()
                .typeForRedirect(typeForDto)
                .build();
    }

    @Override
    @Transactional
    public CommunityForModifyResponseDto getCommunityPost(Long communityId) {
        Community found = communityRepository.findById(communityId).get();
        found.upViewCount();

        return CommunityForModifyResponseDto.builder()
                .id(found.getId())
                .type(found.getType())
                .state(found.getState())
                .title(found.getTitle())
                .createdAt(found.getCreatedAt())
                .updatedAt(found.getUpdatedAt())
                .viewCount(found.getViewCount())
                .content(found.getContent())
                .tags(found.getTags())
                .bookmarkCount(found.getBookmarks().size())
                .build();
    }

    @Override
    @Transactional
    public CommunityTypeResponseDto updatePost(Long communityId, CommunityUpdateRequestDto postToUpdate) {
        List<String> splitTagStringToUpdate;

        boolean isTagEmpty = postToUpdate.getTags().trim().isEmpty();
        if (isTagEmpty) {
            splitTagStringToUpdate = null;
        } else {
            splitTagStringToUpdate = Arrays.stream(postToUpdate.getTags().split(","))
                    .toList();
        }

        Community updated = Community.builder()
                .id(communityId)
                .title(postToUpdate.getTitle())
                .tags(splitTagStringToUpdate)
                .content(postToUpdate.getContent())
                .build();
        updated.setBoardType(postToUpdate.getType());
        updated.setDefaultJoinState();
        communityRepository.save(updated);

        // 리다이렉트할 때 넣어줄 글타입
        String typeForDto = setTypeForRedirect(updated);

        return CommunityTypeResponseDto.builder()
                .typeForRedirect(typeForDto)
                .build();
    }

    private static String setTypeForRedirect(Community resultCommunity) {
        String typeForDto;
        if (resultCommunity.getType() == BoardType.PARTY) {
            typeForDto = "parties";
        } else  {
            typeForDto = "chats";
        }
        return typeForDto;
    }


}
