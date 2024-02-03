package com.donguri.jejudorang.domain.community.service;

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

import java.util.Arrays;
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
    public Map<String, Object> getPartyPostList(Pageable pageable, String paramState, String searchWord, String searchTag) {
        Map<String, Object> resultMap = new HashMap<>();

        // 넘어온 String state -> null / Enum 변환
        JoinState state = setStateToSort(paramState);

        // 검색어가 null인데 null처리 안되는 경우 처리
        if (searchWord != null && searchWord.trim().isEmpty()) {
            searchWord = null;
        }

        // 태그 공백일 경우 null처리
        List<String> splitTagsToSearch =
                (searchTag != null && !searchTag.isEmpty()) ?
                        Arrays.asList(searchTag.split(","))
                        : null;

        int allPartyPageCount;
        Page<Community> partyEntityList;

        // 모든 모임글 (전체)
        if (state == null) {
            partyEntityList =
                    (searchWord != null && splitTagsToSearch != null) ?
                            communityRepository.findAllByTypeContainingWordAndTag(BoardType.PARTY, searchWord, splitTagsToSearch, splitTagsToSearch.size(), pageable)
                            : (searchWord == null && splitTagsToSearch == null) ?
                            communityRepository.findAllByType(BoardType.PARTY, pageable)
                            : (searchWord != null) ?
                            communityRepository.findAllByTypeContainingWord(BoardType.PARTY, searchWord, pageable)
                            : communityRepository.findAllByTypeContainingTag(BoardType.PARTY, splitTagsToSearch, splitTagsToSearch.size(), pageable);

            allPartyPageCount =
                    (searchWord != null && splitTagsToSearch != null) ?
                            communityRepository.findAllByTypeContainingWordAndTag(BoardType.PARTY, searchWord, splitTagsToSearch, splitTagsToSearch.size(), pageable).getTotalPages()
                            : (searchWord == null && splitTagsToSearch == null) ?
                            communityRepository.findAllByType(BoardType.PARTY, pageable).getTotalPages()
                            : (searchWord != null) ?
                            communityRepository.findAllByTypeContainingWord(BoardType.PARTY, searchWord, pageable).getTotalPages()
                            : communityRepository.findAllByTypeContainingTag(BoardType.PARTY, splitTagsToSearch, splitTagsToSearch.size(), pageable).getTotalPages();

        // 상태 존재 (모집중 or 모집완료)
        } else {
            partyEntityList =
                    (searchWord != null && splitTagsToSearch != null) ?
                            communityRepository.findAllByTypeAndStateContainingWordAndTag(BoardType.PARTY, state, searchWord, splitTagsToSearch, splitTagsToSearch.size(), pageable)
                            : (searchWord == null && splitTagsToSearch == null) ?
                            communityRepository.findAllByTypeAndState(BoardType.PARTY, state, pageable)
                            : (searchWord != null) ?
                            communityRepository.findAllByTypeAndStateContainingWord(BoardType.PARTY, state, searchWord, pageable)
                            : communityRepository.findAllByTypeAndStateContainingTag(BoardType.PARTY, state, splitTagsToSearch, splitTagsToSearch.size(), pageable);

            allPartyPageCount =
                    (searchWord != null && splitTagsToSearch != null) ?
                            communityRepository.findAllByTypeAndStateContainingWordAndTag(BoardType.PARTY, state, searchWord, splitTagsToSearch, splitTagsToSearch.size(), pageable).getTotalPages()
                            : (searchWord == null && splitTagsToSearch == null) ?
                            communityRepository.findAllByTypeAndState(BoardType.PARTY, state, pageable).getTotalPages()
                            : (searchWord != null) ?
                            communityRepository.findAllByTypeAndStateContainingWord(BoardType.PARTY, state, searchWord, pageable).getTotalPages()
                            : communityRepository.findAllByTypeAndStateContainingTag(BoardType.PARTY, state, splitTagsToSearch, splitTagsToSearch.size(), pageable).getTotalPages();


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
    public void changePartyJoinState(Long communityId) {
        Community toUpdateState = communityRepository.findById(communityId).get();
        toUpdateState.changeJoinState();
    }
}
