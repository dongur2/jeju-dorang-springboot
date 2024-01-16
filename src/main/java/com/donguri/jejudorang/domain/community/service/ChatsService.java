package com.donguri.jejudorang.domain.community.service;

import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ChatsService {
    Map<String, Object> getChatPostList(Pageable pageable);
}
