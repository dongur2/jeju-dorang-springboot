package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.response.ChatListResponseDto;
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
import java.util.List;
import java.util.Map;


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

        Page<PartyListResponseDto> partyListDtoPage = partyEntityList.map(
                party -> PartyListResponseDto.from(party, party.getTags().stream().map(
                                tag -> tag.getTag().getKeyword())
                        .toList())
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

        List<String> tagsToStringList = null;
        if (foundParty.getTags() != null) {
            tagsToStringList = foundParty.getTags().stream().map(
                            communityWithTag -> communityWithTag.getTag().getKeyword())
                    .toList();
        }

        return PartyDetailResponseDto.from(foundParty, tagsToStringList);
    }

    @Override
    @Transactional
    public void changePartyJoinState(Long communityId) {
        Community toUpdateState = communityRepository.findById(communityId).get();
        toUpdateState.changeJoinState();
    }
}
