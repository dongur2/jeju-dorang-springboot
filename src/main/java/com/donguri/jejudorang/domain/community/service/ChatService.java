package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.response.ChatListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ChatService {

    Page<ChatListResponse> getChatPostList(Pageable pageable, String searchWord, String searchTag);

}
