package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.response.ChatListResponse;
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
import java.util.List;


@Slf4j
@Service
public class ChatServiceI implements ChatService {
    @Autowired private final CommunityRepository communityRepository;
    public ChatServiceI(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    @Override
    @Transactional
    public Page<ChatListResponse> getChatPostList(Pageable pageable, String searchWord, String searchTag) {

        // 검색어가 null인데 null처리 안되는 경우 처리
        if (searchWord != null && searchWord.trim().isEmpty()) {
            searchWord = null;
        }

        // 태그 공백일 경우 null처리
        List<String> splitTagsToSearch =
                (searchTag != null && !searchTag.isEmpty()) ?
                        Arrays.asList(searchTag.split(","))
                        : null;

        Page<Community> entities;

        // 1. 검색어 없이 태그만 검색할 경우
        if (searchWord == null) {
            if (splitTagsToSearch != null) {
                entities = communityRepository.findAllByTypeContainingTag(BoardType.CHAT, splitTagsToSearch, splitTagsToSearch.size(), pageable);
            } else {
                entities = communityRepository.findAllByType(BoardType.CHAT, pageable);
            }

        // 2. 검색어와 태그 동시 검색할 경우
        } else {
            if (splitTagsToSearch != null) {
                entities = communityRepository.findAllByTypeContainingWordAndTag(BoardType.CHAT, searchWord, splitTagsToSearch, splitTagsToSearch.size(), pageable);
            } else {
                entities = communityRepository.findAllByTypeContainingWord(BoardType.CHAT, searchWord, pageable);
            }
        }

        // entity -> dto
        return entities.map(
                chat -> ChatListResponse.from(chat, chat.getTags().stream()
                        .map(tag -> tag.getTag().getKeyword())
                        .toList())
        );

    }

}
