package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.response.CommunityListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PartyService {

    // 모임 글목록 조회: 모집상태 정렬/검색어/태그 검색 포함
    Page<CommunityListResponse> getPartyPostList(Pageable pageable, String partyState, String searchWord, String searchTag);

    // 모집상태 변경: 모집중/모집완료
    void changePartyJoinState(Long communityId);

}
