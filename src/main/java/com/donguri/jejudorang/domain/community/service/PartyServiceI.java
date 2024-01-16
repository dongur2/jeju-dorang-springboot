package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.response.PartyDetailResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.PartyListResponseDto;
import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.donguri.jejudorang.global.common.DateFormat.calculateTime;

@Slf4j
@Service
public class PartyServiceI implements PartyService{
    @Autowired
    CommunityRepository communityRepository;

    @Override
    public Map<String, Object> getPartyPostList(Pageable pageable, String partyState) {
        Map<String, Object> resultMap = new HashMap<>();

        int allPartyPageCount;
        Page<Community> partyEntityList;

        // 모든 모임글
        if (partyState == null) {
            log.info("partyState={}", partyState);
            // 전체 페이지 수
            allPartyPageCount = communityRepository.findAllByType(BoardType.PARTY, pageable).getTotalPages();
            // 데이터
            partyEntityList = communityRepository.findAllByType(BoardType.PARTY, pageable);

            // 모집중, 모집완료 전체 모임글
        } else {
            // String으로 넘어온 state 변환
            JoinState state;
            if (partyState.equals("recruiting")) {
                state = JoinState.RECRUITING;
            } else {
                state = JoinState.DONE;
            }
            log.info("joinstate={}", state);

            allPartyPageCount = communityRepository.findAllByTypeAndState(BoardType.PARTY, state, pageable).getTotalPages();
            partyEntityList = communityRepository.findAllByTypeAndState(BoardType.PARTY, state, pageable);
        }

        Page<PartyListResponseDto> partyListDtoPage =
                partyEntityList.map(party -> PartyListResponseDto.builder()
                        .id(party.getId())
                        .type(party.getType())
                        .state(party.getState())
                        .title(party.getTitle())
                        .createdAt(calculateTime(party.getCreatedAt())) // 포맷 변경
                        .viewCount(party.getViewCount())
                        .tags(party.getTags())
                        .bookmarkCount(party.getBookmarks().size())
                        .build()
                );

        resultMap.put("allPartyPageCount", allPartyPageCount);
        resultMap.put("partyListDtoPage", partyListDtoPage);

        return resultMap;
    }

    @Override
    public PartyDetailResponseDto getPartyPost(Long communityId) {
        Community foundParty = communityRepository.findById(communityId).get();
        foundParty.upViewCount();

        return PartyDetailResponseDto.builder()
                .id(foundParty.getId())
                .type(foundParty.getType())
                .state(foundParty.getState())
                .title(foundParty.getTitle())
                .createdAt(foundParty.getCreatedAt())
                .updatedAt(foundParty.getUpdatedAt())
                .viewCount(foundParty.getViewCount())
                .content(foundParty.getContent())
                .tags(foundParty.getTags())
                .bookmarkCount(foundParty.getBookmarks().size())
                .build();
    }

    @Override
    @Transactional
    public void changePartyJoinState(Long communityId) {
        Community toUpdateState = communityRepository.findById(communityId).get();
        toUpdateState.changeJoinState();
    }
}
