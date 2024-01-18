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
        if (!tagString.trim().isEmpty()) { // 공백이 넘어왔을 경우 한 번 더 필터

            // String -> List<String>으로 변환 후 각 키워드 저장
            Arrays.stream(tagString.split(",")).toList()
                    // Tag 테이블에 이미 존재하는 키워드인지 중복 확인 후 새로운 키워드라면 저장
                    .stream().map(tag -> tagService.checkDuplicated(tag)
                            .orElseGet(() -> tagService.saveNewTag(tag))
                    )
                    // 커뮤니티&태그 쌍이 CommunityWithTag 테이블에 이미 존재하는지 중복 확인 후 아니라면 저장 - 글 수정 메서드
                    .forEach(tag -> checkDuplicatedMap(community, tag)
                            .orElseGet(() -> saveCommunityWithTagMap(community, tag))
                    );
        }
    }

    private Optional<CommunityWithTag> checkDuplicatedMap(Community community, Tag tag) {
        return communityWithTagRepository.findByCommunityAndTag(community, tag);
    }

    private CommunityWithTag saveCommunityWithTagMap(Community community, Tag tag) {
        return communityWithTagRepository.save(new CommunityWithTag(community, tag));
    }

}
