package com.donguri.jejudorang.domain.bookmark.service;

import com.donguri.jejudorang.domain.bookmark.repository.BookmarkRepository;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.service.CommunityService;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookmarkServiceITest {
    @Autowired
    BookmarkService bookmarkService;
    @Autowired
    CommunityService communityService;

    @Autowired
    CommunityRepository communityRepository;
    @Autowired
    BookmarkRepository bookmarkRepository;
    @Autowired
    UserRepository userRepository;


    @Test
    void 유저_북마크_테스트() {
        // given
//        User testUser = userRepository.save(new User());
        Community testCommunity = Community.builder().title("제목").content("본문").build();
        testCommunity.setBoardType("party");
        testCommunity.setDefaultJoinState();
        Community saved = communityRepository.save(testCommunity);

        // when
        bookmarkService.changeCommunityLikedState(userRepository.findById(1L).get(), testCommunity.getId());
        Community community = communityRepository.findById(testCommunity.getId()).get();

        // then
        Assertions.assertThat(community.getBookmarks().size()).isEqualTo(1);
    }

}