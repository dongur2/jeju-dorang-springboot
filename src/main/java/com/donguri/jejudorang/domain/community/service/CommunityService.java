package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.request.CommunityUpdateRequestDto;
import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityDetailResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface CommunityService {

    // getPartyList
    Map<String, Object> getPartyPostList(Pageable pageable, String partyState);




    CommunityDetailResponseDto getPost(Long id);

    void savePost(CommunityWriteRequestDto post);

    void updatePost(Long id, CommunityUpdateRequestDto post);

    void changePartyJoinState(Long id);

}
