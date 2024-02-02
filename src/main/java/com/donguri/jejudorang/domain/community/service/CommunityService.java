package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.bookmark.entity.Bookmark;
import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityForModifyResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityTypeResponseDto;

public interface CommunityService {

    //postNewCommunity
    CommunityTypeResponseDto saveNewPost(CommunityWriteRequestDto postToSave, String accessToken);

    //getBoardModifyForm
    CommunityForModifyResponseDto getCommunityPost(Long communityId);

    //modifyCommunity
    CommunityTypeResponseDto updatePost(Long communityId, CommunityWriteRequestDto postToUpdate);

    void updateBookmarkState(Bookmark bookmark);
}
