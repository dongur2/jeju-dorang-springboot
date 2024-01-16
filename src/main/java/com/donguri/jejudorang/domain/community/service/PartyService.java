package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.response.PartyDetailResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface PartyService {
    Map<String, Object> getPartyPostList(Pageable pageable, String partyState);
    PartyDetailResponseDto getPartyPost(Long communityId);
}
