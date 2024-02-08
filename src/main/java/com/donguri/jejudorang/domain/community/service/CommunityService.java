package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityTypeResponseDto;
import com.donguri.jejudorang.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface CommunityService {

    //postNewCommunity
    CommunityTypeResponseDto saveNewPost(CommunityWriteRequestDto postToSave, String accessToken);


    //getCommunity
    Map<String, Object> getCommunityPost(Long communityId, boolean forModify, HttpServletRequest request);

    //마이페이지: 작성한 게시글 조회
    Map<String, Object> getAllPostsWrittenByUser(User writer, Pageable pageable);


    //modifyCommunity
    CommunityTypeResponseDto updatePost(Long communityId, CommunityWriteRequestDto postToUpdate);


    //updateView
    void updateView(Long communityId);


    //회원 탈퇴시 작성글과 작성자 연관 관계 삭제 - null 처리
    void findAllPostsByUserAndSetWriterNull(Long userId);

}
