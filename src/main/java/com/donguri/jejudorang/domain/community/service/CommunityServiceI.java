package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.request.CommunityUpdateRequestDto;
import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityDetailResponseDto;
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
    public CommunityDetailResponseDto getPost(Long id) {
        Community found = communityRepository.findById(id).get();
        found.upViewCount();

        return CommunityDetailResponseDto.builder()
                .id(found.getId())
                .type(found.getType())
                .state(found.getState())
                .title(found.getTitle())
                .createdAt(found.getCreatedAt())
                .updatedAt(found.getUpdatedAt())
                .viewCount(found.getViewCount())
                .content(found.getContent())
                .tags(found.getTags())
                .likedCount(found.getLiked().size())
                .build();
    }

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
        String typeForDto;
        if (saved.getType() == BoardType.PARTY) {
            typeForDto = "parties";
        } else  {
            typeForDto = "chats";
        }

        return CommunityTypeResponseDto.builder()
                .typeForRedirect(typeForDto)
                .build();
    }

    @Override
    @Transactional
    public void updatePost(Long id, CommunityUpdateRequestDto post) {
        List<String> splitTagStringToUpdate;

        boolean isTagEmpty = post.getTags().trim().isEmpty();
        if (isTagEmpty) {
            splitTagStringToUpdate = null;
        } else {
            splitTagStringToUpdate = Arrays.stream(post.getTags().split(","))
                    .toList();
        }

        Community update = Community.builder()
                .id(id)
                .title(post.getTitle())
                .tags(splitTagStringToUpdate)
                .content(post.getContent())
                .build();
        update.setBoardType(post.getType());
        update.setDefaultJoinState();
        communityRepository.save(update);
    }

    @Override
    @Transactional
    public void changePartyJoinState(Long id) {
        Community found = communityRepository.findById(id).get();
        found.changeJoinState();
    }
}
