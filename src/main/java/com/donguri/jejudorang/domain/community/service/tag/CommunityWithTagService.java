package com.donguri.jejudorang.domain.community.service.tag;

import com.donguri.jejudorang.domain.community.entity.Community;

public interface CommunityWithTagService {
    void saveTagToPost(Community community, String tagString);

}
