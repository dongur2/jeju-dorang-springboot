package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.response.PartyDetailResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.PartyListResponseDto;
import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.Join;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.donguri.jejudorang.global.common.DateFormat.calculateTime;

@Slf4j
@Service
public class PartyServiceI implements PartyService{
    @Autowired
    CommunityRepository communityRepository;

    @Override
    @Transactional
    public Map<String, Object> getPartyPostList(Pageable pageable, String paramState, String searchWord) {
        Map<String, Object> resultMap = new HashMap<>();

        // 넘어온 String state -> null / Enum 변환
        JoinState state = setStateToSort(paramState);

        int allPartyPageCount;
        Page<Community> partyEntityList;

        // 모든 모임글 (전체)
        if (state == null) {
            // 검색어가 존재할 경우
            if (searchWord != null) {
                allPartyPageCount = communityRepository.findAllPartiesWithSearchWord(BoardType.PARTY, searchWord, pageable).getTotalPages(); // 전체 페이지 수
                partyEntityList = communityRepository.findAllPartiesWithSearchWord(BoardType.PARTY, searchWord, pageable); // 데이터

            // 검색어가 없을 경우
            } else {
                allPartyPageCount = communityRepository.findAllByType(BoardType.PARTY, pageable).getTotalPages(); // 전체 페이지 수
                partyEntityList = communityRepository.findAllByType(BoardType.PARTY, pageable); // 데이터
            }

        // 상태 존재 (모집중 or 모집완료)
        } else {
            // 검색어가 존재할 경우
            if (searchWord != null) {
                allPartyPageCount = communityRepository.findAllPartiesWithTypeAndSearchWord(BoardType.PARTY, state, searchWord, pageable).getTotalPages(); // 전체 페이지 수
                partyEntityList = communityRepository.findAllPartiesWithTypeAndSearchWord(BoardType.PARTY, state, searchWord, pageable); // 데이터

            // 검색어가 없을 경우
            } else {
                allPartyPageCount = communityRepository.findAllByTypeAndState(BoardType.PARTY, state, pageable).getTotalPages();
                partyEntityList = communityRepository.findAllByTypeAndState(BoardType.PARTY, state, pageable);
            }
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

    private static JoinState setStateToSort(String paramState) {
        if (paramState.equals("all")) {
            return null;
        } else if (paramState.equals("recruiting")) {
            return JoinState.RECRUITING;
        } else {
            return JoinState.DONE;
        }
    }

    @Override
    @Transactional
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
