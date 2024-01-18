package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.response.PartyDetailResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface PartyService {

    //getPartyList
    Map<String, Object> getPartyPostList(Pageable pageable, String partyState, String searchWord, String searchTag);

    //getPartyDetail
    PartyDetailResponseDto getPartyPost(Long communityId);

    //modifyBoardJoinState
    void changePartyJoinState(Long communityId);

}
