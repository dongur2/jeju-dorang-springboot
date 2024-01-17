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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class ChatServiceI implements ChatService {
    @Autowired
    CommunityRepository communityRepository;

    @Override
    @Transactional
    public Map<String, Object> getChatPostList(Pageable pageable, String searchWord, String searchTags) {
        Map<String, Object> resultMap = new HashMap<>();

        // 검색어가 null인데 null처리 안되는 경우 처리
        if (searchWord != null && searchWord.trim().isEmpty()) {
            searchWord = null;
        }

        // 태그 공백일 경우 null처리
        List<String> splitTagsToSearch =
                (searchTags != null && !searchTags.trim().isEmpty()) ?
                        Arrays.asList(searchTags.split(","))
                        : null;

        int allChatPageCount;
        Page<Community> chatEntityList;

        log.info("word={}, tag={}", searchWord, searchTags);

        if (searchWord == null) {
            log.info("검색어 없음");

            // tag
            if (splitTagsToSearch != null) {
                log.info("태그 존재");
                allChatPageCount = communityRepository.findAllChatsWithTag(BoardType.CHAT, splitTagsToSearch,pageable).getTotalPages();
                chatEntityList = communityRepository.findAllChatsWithTag(BoardType.CHAT, splitTagsToSearch, pageable);
            } else {
                log.info("태그 없음");
                allChatPageCount = communityRepository.findAllByType(BoardType.CHAT, pageable).getTotalPages();
                chatEntityList = communityRepository.findAllByType(BoardType.CHAT, pageable);
            }

        } else {
            log.info("검색어 존재");
            allChatPageCount = communityRepository.findAllChatsWithSearchWord(BoardType.CHAT, searchWord, pageable).getTotalPages();
            chatEntityList = communityRepository.findAllChatsWithSearchWord(BoardType.CHAT, searchWord, pageable);
        }

        Page<ChatListResponseDto> chatListDtoPage = chatEntityList.map(ChatListResponseDto::from);

        resultMap.put("allChatPageCount", allChatPageCount);
        resultMap.put("chatListDtoPage", chatListDtoPage);

        return resultMap;
    }

    @Override
    @Transactional
    public ChatDetailResponseDto getChatPost(Long communityId) {
        Community foundChat = communityRepository.findById(communityId).get();
        foundChat.upViewCount();

        return ChatDetailResponseDto.from(foundChat);
    }
}
