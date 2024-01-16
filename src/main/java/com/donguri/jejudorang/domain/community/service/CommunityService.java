package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.request.CommunityUpdateRequestDto;
import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityDetailResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityTypeResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface CommunityService {

    // postNewCommunity
    CommunityTypeResponseDto saveNewPost(CommunityWriteRequestDto post);




    void updatePost(Long id, CommunityUpdateRequestDto post);

    void changePartyJoinState(Long id);

}
