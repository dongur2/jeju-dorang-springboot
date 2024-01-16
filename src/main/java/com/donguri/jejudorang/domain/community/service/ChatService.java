package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.response.ChatDetailResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ChatService {
    Map<String, Object> getChatPostList(Pageable pageable, String searchWord);
    ChatDetailResponseDto getChatPost(Long id);
}
