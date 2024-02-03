package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.bookmark.entity.Bookmark;
import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityTypeResponseDto;

import java.util.Map;

public interface CommunityService {

    //postNewCommunity
    CommunityTypeResponseDto saveNewPost(CommunityWriteRequestDto postToSave, String accessToken);

    //getCommunity
    Map<String, Object> getCommunityPost(Long communityId, boolean forModify);

    //modifyCommunity
    CommunityTypeResponseDto updatePost(Long communityId, CommunityWriteRequestDto postToUpdate);

    //updateView
    void updateView(Long communityId);

    void updateBookmarkState(Bookmark bookmark);
}
