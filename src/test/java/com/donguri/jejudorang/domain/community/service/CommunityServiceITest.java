package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommunityServiceITest {
    @Autowired
    CommunityRepository communityRepository;
    @Autowired
    CommunityService communityService;

    @Test
    void 임시글쓰기() {
        int num = 1;

        while (num < 100) {
            String title = "진짜잡담제목" + num;
            Community build = Community.builder()
                    .title(title)
                    .content("내용")
                    .build();
            build.setBoardType("chat");
            build.setDefaultJoinState();
            communityRepository.save(build);
            num++;
        }
    }

}