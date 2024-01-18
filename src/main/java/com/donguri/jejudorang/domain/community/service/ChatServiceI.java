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
    public Map<String, Object> getChatPostList(Pageable pageable, String searchWord, String searchTag) {
        Map<String, Object> resultMap = new HashMap<>();

        // 검색어가 null인데 null처리 안되는 경우 처리
        if (searchWord != null && searchWord.trim().isEmpty()) {
            searchWord = null;
        }

        // 태그 공백일 경우 null처리
        List<String> splitTagsToSearch =
                (searchTag != null && !searchTag.isEmpty()) ?
                        Arrays.asList(searchTag.split(","))
                        : null;

        int allChatPageCount;
        Page<Community> chatEntityList;

        if (searchWord == null) {
            // tag
            if (splitTagsToSearch != null) {
                allChatPageCount = communityRepository.findAllByTypeContainingTag(BoardType.CHAT, splitTagsToSearch, splitTagsToSearch.size(), pageable).getTotalPages();
                chatEntityList = communityRepository.findAllByTypeContainingTag(BoardType.CHAT, splitTagsToSearch, splitTagsToSearch.size(), pageable);
            } else {
                allChatPageCount = communityRepository.findAllByType(BoardType.CHAT, pageable).getTotalPages();
                chatEntityList = communityRepository.findAllByType(BoardType.CHAT, pageable);
            }

        } else {
            if (splitTagsToSearch != null) {
                allChatPageCount = communityRepository.findAllByTypeContainingWordAndTag(BoardType.CHAT, searchWord, splitTagsToSearch, splitTagsToSearch.size(), pageable).getTotalPages();
                chatEntityList = communityRepository.findAllByTypeContainingWordAndTag(BoardType.CHAT, searchWord, splitTagsToSearch, splitTagsToSearch.size(), pageable);
            } else {
                allChatPageCount = communityRepository.findAllByTypeContainingWord(BoardType.CHAT, searchWord, pageable).getTotalPages();
                chatEntityList = communityRepository.findAllByTypeContainingWord(BoardType.CHAT, searchWord, pageable);
            }
        }


        Page<ChatListResponseDto> chatListDtoPage = chatEntityList.map(
                chat -> ChatListResponseDto.from(chat, chat.getTags().stream().map(
                        tag -> tag.getTag().getKeyword())
                        .toList())
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

        List<String> tagsToStringList = null;
        if (foundChat.getTags() != null) {
            tagsToStringList = foundChat.getTags().stream().map(
                            communityWithTag -> communityWithTag.getTag().getKeyword())
                    .toList();
        }

        return ChatDetailResponseDto.from(foundChat, tagsToStringList);
    }
}
