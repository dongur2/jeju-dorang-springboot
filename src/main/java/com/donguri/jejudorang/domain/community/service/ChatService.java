package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.response.ChatListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ChatService {

    Page<ChatListResponseDto> getChatPostList(Pageable pageable, String searchWord, String searchTag);

}
