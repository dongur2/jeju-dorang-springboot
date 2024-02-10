package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityListResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityTypeResponseDto;
import com.donguri.jejudorang.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface CommunityService {

    // 커뮤니티 새 글 작성
    CommunityTypeResponseDto saveNewPost(CommunityWriteRequestDto postToSave, String accessToken);

    // 커뮤니티 글 수정
    CommunityTypeResponseDto updatePost(Long communityId, CommunityWriteRequestDto postToUpdate);

    // 커뮤니티 상세글 조회: 수정/조회 구분
    Map<String, Object> getCommunityPost(Long communityId, boolean forModify, HttpServletRequest request);

    // 커뮤니티 상세글 조회시 조회수 증가 업데이트
    void updateView(Long communityId);

    // 커뮤니티글 삭제
    void deleteCommunityPost(String accessToken, Long communityId);



    // 마이페이지: 작성한 게시글 조회
    Page<CommunityListResponseDto> getAllPostsWrittenByUser(User writer, Pageable pageable);

    // 회원 탈퇴시 작성글과 작성자 연관 관계 삭제 - null 처리
    void findAllPostsByUserAndSetWriterNull(Long userId);


}
