package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.response.ChatDetailResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.ChatListResponseDto;
import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.donguri.jejudorang.global.common.DateFormat.calculateTime;

@Slf4j
@Service
public class ChatServiceI implements ChatService {
    @Autowired
    CommunityRepository communityRepository;

    @Override
    public Map<String, Object> getChatPostList(Pageable pageable) {
        Map<String, Object> resultMap = new HashMap<>();

        int allChatPageCount;
        Page<Community> chatEntityList;

        // 전체 페이지 수
        allChatPageCount = communityRepository.findAllByType(BoardType.CHAT, pageable).getTotalPages();
        // 데이터
        chatEntityList = communityRepository.findAllByType(BoardType.CHAT, pageable);

        Page<ChatListResponseDto> chatListDtoPage =
                chatEntityList.map(board -> ChatListResponseDto.builder()
                        .id(board.getId())
                        .type(board.getType())
                        .title(board.getTitle())
                        .createdAt(calculateTime(board.getCreatedAt())) // 포맷 변경
                        .viewCount(board.getViewCount())
                        .tags(board.getTags())
                        .likedCount(board.getLiked().size())
                        .build()
                );

        resultMap.put("allChatPageCount", allChatPageCount);
        resultMap.put("chatListDtoPage", chatListDtoPage);

        return resultMap;
    }

    @Override
    public ChatDetailResponseDto getChatPost(Long communityId) {
        Community found = communityRepository.findById(communityId).get();
        found.upViewCount();

        return ChatDetailResponseDto.builder()
                .id(found.getId())
                .type(found.getType())
                .title(found.getTitle())
                .createdAt(found.getCreatedAt())
                .updatedAt(found.getUpdatedAt())
                .viewCount(found.getViewCount())
                .content(found.getContent())
                .tags(found.getTags())
                .likedCount(found.getLiked().size())
                .build();
    }
}
