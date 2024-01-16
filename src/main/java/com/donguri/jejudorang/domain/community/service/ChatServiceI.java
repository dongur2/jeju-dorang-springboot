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
                chatEntityList.map(chat -> ChatListResponseDto.builder()
                        .id(chat.getId())
                        .type(chat.getType())
                        .title(chat.getTitle())
                        .createdAt(calculateTime(chat.getCreatedAt())) // 포맷 변경
                        .viewCount(chat.getViewCount())
                        .tags(chat.getTags())
                        .bookmarkCount(chat.getBookmarks().size())
                        .build()
                );

        resultMap.put("allChatPageCount", allChatPageCount);
        resultMap.put("chatListDtoPage", chatListDtoPage);

        return resultMap;
    }

    @Override
    @Transactional
    public ChatDetailResponseDto getChatPost(Long communityId) {
        Community foundChat = communityRepository.findById(communityId).get();
        foundChat.upViewCount();

        return ChatDetailResponseDto.builder()
                .id(foundChat.getId())
                .type(foundChat.getType())
                .title(foundChat.getTitle())
                .createdAt(foundChat.getCreatedAt())
                .updatedAt(foundChat.getUpdatedAt())
                .viewCount(foundChat.getViewCount())
                .content(foundChat.getContent())
                .tags(foundChat.getTags())
                .bookmarkCount(foundChat.getBookmarks().size())
                .build();
    }
}
