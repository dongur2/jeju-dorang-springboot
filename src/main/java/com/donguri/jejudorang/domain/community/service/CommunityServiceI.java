package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.bookmark.entity.Bookmark;
import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityForModifyResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityTypeResponseDto;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.service.tag.CommunityWithTagService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Book;
import java.util.List;

@Service
@Slf4j
public class CommunityServiceI implements CommunityService {
    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityWithTagService communityWithTagService;

    @Override
    @Transactional
    public CommunityTypeResponseDto saveNewPost(CommunityWriteRequestDto postToWrite) {

        // 태그 제외 entity 생성 & 저장
        Community communityToWrite = postToWrite.toEntity();
        Community saved = communityRepository.save(communityToWrite);

        // 작성된 태그가 존재할 경우에만 태그 저장 메서드 실행
        if (postToWrite.tags() != null) {
            communityWithTagService.saveTagToPost(saved, postToWrite.tags());
        }

        // 리다이렉트할 때 넣어줄 글타입
        String typeForDto = setTypeForRedirect(saved);
        return new CommunityTypeResponseDto(typeForDto);
    }

    @Override
    @Transactional
    public CommunityForModifyResponseDto getCommunityPost(Long communityId) {
        Community existingCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("다음 ID에 해당하는 글을 찾을 수 없습니다: " + communityId));
        existingCommunity.upViewCount();

        List<String> tagsToStringList = existingCommunity.getTags().stream().map(
                        communityWithTag -> communityWithTag.getTag().getKeyword())
                        .toList();

        return CommunityForModifyResponseDto.from(existingCommunity, tagsToStringList);
    }

    @Override
    @Transactional
    public CommunityTypeResponseDto updatePost(Long communityId, CommunityWriteRequestDto postToUpdate) {

        Community existingCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("다음 ID에 해당하는 글을 찾을 수 없습니다: " + communityId));

        // 제목, 글분류, 글내용, 모집상태 업데이트 (dirty checking)
        existingCommunity.update(postToUpdate);

        // 태그 업데이트
        if (postToUpdate.tags() != null) {
            communityWithTagService.saveTagToPost(existingCommunity, postToUpdate.tags());
        }

        // 리다이렉트할 때 넣어줄 글타입
        String typeForDto = setTypeForRedirect(existingCommunity);
        return new CommunityTypeResponseDto(typeForDto);
    }

    @Override
    @Transactional
    public Community updateBookmarkState(Bookmark bookmark) {
        Community community = bookmark.getCommunity();
        community.updateBookmarks(bookmark); // 없으면 추가, 있으면 삭제
        return community;
    }


    private static String setTypeForRedirect(Community resultCommunity) {
        if (resultCommunity.getType() == BoardType.PARTY) {
            return "parties";
        } else  {
            return "chats";
        }
    }


}
