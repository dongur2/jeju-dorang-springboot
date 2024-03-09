package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.response.CommunityListResponse;
import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.global.error.CustomErrorCode;
import com.donguri.jejudorang.global.error.CustomException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;


@Slf4j
@Service
public class PartyServiceI implements PartyService{
    @Autowired private final CommunityRepository communityRepository;
    public PartyServiceI(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    @Override
    @Transactional
    public Page<CommunityListResponse> getPartyPostList(Pageable pageable, String paramState, String searchWord, String searchTag) {

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

        Page<Community> entities;

        // 1. 모든 모집상태를 포함한 모임글
        if (state == null) {
            entities =
                    (searchWord != null && splitTagsToSearch != null) ?
                            communityRepository.findAllByTypeContainingWordAndTag(BoardType.PARTY, searchWord, splitTagsToSearch, splitTagsToSearch.size(), pageable)
                            : (searchWord == null && splitTagsToSearch == null) ?
                            communityRepository.findAllByType(BoardType.PARTY, pageable)
                            : (searchWord != null) ?
                            communityRepository.findAllByTypeContainingWord(BoardType.PARTY, searchWord, pageable)
                            : communityRepository.findAllByTypeContainingTag(BoardType.PARTY, splitTagsToSearch, splitTagsToSearch.size(), pageable);

        // 2. 상태 존재 (모집중 or 모집완료)
        } else {
            entities =
                    (searchWord != null && splitTagsToSearch != null) ?
                            communityRepository.findAllByTypeAndStateContainingWordAndTag(BoardType.PARTY, state, searchWord, splitTagsToSearch, splitTagsToSearch.size(), pageable)
                            : (searchWord == null && splitTagsToSearch == null) ?
                            communityRepository.findAllByTypeAndState(BoardType.PARTY, state, pageable)
                            : (searchWord != null) ?
                            communityRepository.findAllByTypeAndStateContainingWord(BoardType.PARTY, state, searchWord, pageable)
                            : communityRepository.findAllByTypeAndStateContainingTag(BoardType.PARTY, state, splitTagsToSearch, splitTagsToSearch.size(), pageable);
        }

        // entity -> dto
        return entities.map(
                party -> CommunityListResponse.from(party, party.getTags().stream()
                        .map(tag -> tag.getTag().getKeyword())
                        .toList())
        );

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
        try {
            Community toUpdateState = communityRepository.findById(communityId)
                    .orElseThrow(() -> new CustomException(CustomErrorCode.COMMUNITY_NOT_FOUND));
            toUpdateState.changeJoinState();

        } catch (Exception e) {
            log.error("모집상태 변경 실패: {}", e.getMessage());
            throw e;
        }
    }

}
