package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.request.CommunityUpdateRequestDto;
import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityDetailResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityListResponseDto;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.donguri.jejudorang.global.common.DateFormat.calculateTime;

@Service
@Slf4j
public class CommunityServiceI implements CommunityService {
    @Autowired
    private CommunityRepository boardRepository;

    @Override
    public Map<String, Object> getAllPosts(Pageable pageable, String boardType) {
        Map<String, Object> returnMap = new HashMap<>();

        BoardType findType;
        if (boardType.equals("party")) {
            findType = BoardType.PARTY;
        } else {
            findType = BoardType.CHAT;
        }

        Integer allBoardPageCount = boardRepository.findAllByType(findType, pageable).getTotalPages();
        Page<Community> boardEntityList = boardRepository.findAllByType(findType, pageable);
        Page<CommunityListResponseDto> boardDtoList =
                boardEntityList.map(board -> CommunityListResponseDto.builder()
                        .id(board.getId())
                        .type(board.getType())
                        .state(board.getState())
                        .title(board.getTitle())
                        .createdAt(calculateTime(board.getCreatedAt())) // 포맷 변경
                        .viewCount(board.getViewCount())
                        .tags(board.getTags())
                        .likedCount(board.getLiked().size())
                        .build()
                );

        returnMap.put("boardCounts", allBoardPageCount);
        returnMap.put("boardPage", boardDtoList);

        return returnMap;
    }

    @Override
    @Transactional
    public CommunityDetailResponseDto getPost(Long id) {
        Community found = boardRepository.findById(id).get();
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
    public void savePost(CommunityWriteRequestDto post) {
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

        boardRepository.save(newPost);
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
        boardRepository.save(update);
    }

    @Override
    @Transactional
    public void changePartyJoinState(Long id) {
        Community found = boardRepository.findById(id).get();
        found.changeJoinState();
    }
}
