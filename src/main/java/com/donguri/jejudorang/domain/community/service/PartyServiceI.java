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
                partyEntityList.map(board -> PartyListResponseDto.builder()
                        .id(board.getId())
                        .type(board.getType())
                        .state(board.getState())
                        .title(board.getTitle())
                        .createdAt(calculateTime(board.getCreatedAt())) // 포맷 변경
                        .viewCount(board.getViewCount())
                        .tags(board.getTags())
                        .likedCount(board.getLiked().size())
                        .build()
                );

        resultMap.put("allPartyPageCount", allPartyPageCount);
        resultMap.put("partyListDtoPage", partyListDtoPage);

        return resultMap;
    }

    @Override
    public PartyDetailResponseDto getPartyPost(Long communityId) {
        Community found = communityRepository.findById(communityId).get();
        found.upViewCount();

        return PartyDetailResponseDto.builder()
                .id(found.getId())
                .type(found.getType())
                .state(found.getState())
                .title(found.getTitle())
                .createdAt(found.getCreatedAt())
                .updatedAt(found.getUpdatedAt())
                .viewCount(found.getViewCount())
                .content(found.getContent())
                .tags(found.getTags())
                .likedCount(found.getLiked().size())
                .build();
    }
}
