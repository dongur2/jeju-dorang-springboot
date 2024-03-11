package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequest;
import com.donguri.jejudorang.domain.community.dto.response.CommunityMyPageListResponse;
import com.donguri.jejudorang.domain.community.dto.response.CommunityTypeResponse;
import com.donguri.jejudorang.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CommunityService {

    // 커뮤니티 새 글 작성
    CommunityTypeResponse saveNewPost(CommunityWriteRequest postToSave, String accessToken);

    // 커뮤니티 글 수정
    CommunityTypeResponse updatePost(Long communityId, CommunityWriteRequest postToUpdate);

    // 커뮤니티 상세글 조회: 수정/조회 구분
    Map<String, Object> getCommunityPost(Long communityId, boolean forModify, HttpServletRequest request);

    // 커뮤니티 상세글 조회시 조회수 증가 업데이트
    void updateView(Long communityId);

    // 커뮤니티글 삭제
    void deleteCommunityPost(String accessToken, Long communityId);



    // 마이페이지: 작성한 게시글 조회
    Page<CommunityMyPageListResponse> getAllPostsWrittenByUser(User writer, Pageable pageable);

    // 마이페이지: 댓글단 글 조회
    Page<CommunityMyPageListResponse> getAllPostsWithCommentsByUser(User writer, Pageable pageable);

    // 회원 탈퇴시 작성글과 작성자 연관 관계 삭제 - null 처리
    void findAllPostsByUserAndSetWriterNull(Long userId);


    // 관리자: 사용되지 않는 이미지 삭제 위한 콘텐츠 추출
    List<String> getAllContents();
}
