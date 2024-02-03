package com.donguri.jejudorang.domain.community.service;

import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface PartyService {

    //getPartyList
    Map<String, Object> getPartyPostList(Pageable pageable, String partyState, String searchWord, String searchTag);

    //modifyBoardJoinState
    void changePartyJoinState(Long communityId);

}
