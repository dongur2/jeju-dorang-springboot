package com.donguri.jejudorang.domain.community.service.tag;

import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.tag.CommunityWithTag;
import com.donguri.jejudorang.domain.community.entity.tag.Tag;
import com.donguri.jejudorang.domain.community.repository.tag.CommunityWithTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class CommunityWithTagServiceI implements CommunityWithTagService {
    @Autowired
    CommunityWithTagRepository communityWithTagRepository;
    @Autowired
    TagService tagService;

    @Override
    public void saveTagToPost(Community community, String tagString) {
        if (!tagString.trim().isEmpty()) {
            Arrays.stream(tagString.split(",")).toList()
                    .stream().map(tag -> tagService.checkDuplicated(tag)
                            .orElseGet(() -> tagService.saveNewTag(tag)))
                    .forEach(tag -> checkDuplicatedMap(community, tag)
                            .orElseGet(() -> saveCommunityWithTagMap(community, tag)));
        }
    }

    private Optional<CommunityWithTag> checkDuplicatedMap(Community community, Tag tag) {
        return communityWithTagRepository.findByCommunityAndTag(community, tag);
    }

    private CommunityWithTag saveCommunityWithTagMap(Community community, Tag tag) {
        return communityWithTagRepository.save(new CommunityWithTag(community, tag));
    }

}
