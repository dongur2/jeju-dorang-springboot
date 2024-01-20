package com.donguri.jejudorang.domain.bookmark.service;

import com.donguri.jejudorang.domain.bookmark.repository.BookmarkRepository;
import com.donguri.jejudorang.domain.community.dto.response.PartyDetailResponseDto;
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
        User testUser = userRepository.save(new User());
        Community testCommunity = Community.builder().title("제목").content("본문").build();
        testCommunity.setBoardType("party");
        testCommunity.setDefaultJoinState();
        Long saved = communityRepository.save(testCommunity).getId();

        // when
        bookmarkService.changeCommunityBookmarkState(userRepository.findById(1L).get(), testCommunity.getId());

        // then
        Community updated = communityRepository.findById(saved).get();
        Assertions.assertThat(updated.getBookmarks().size()).isEqualTo(1);
    }

    @Test
    void 북마크_삭제() {
        //given
        User testUser = userRepository.findById(1L).get();
        Long existing = communityRepository.findById(4L).get().getId();

        //when
        bookmarkService.changeCommunityBookmarkState(testUser, existing);

        //then
        Community updated = communityRepository.findById(existing).get();
        System.out.println(updated.getBookmarksCount());
        Assertions.assertThat(updated.getBookmarksCount()).isEqualTo(0);
    }

    @Test
    void 북마크_개수_테스트() {
        //given
        Community community = communityRepository.findById(7L).get();
        //when
        PartyDetailResponseDto dto = PartyDetailResponseDto.from(community, null);
        //then
        Assertions.assertThat(community.getBookmarksCount()).isEqualTo(1);
        Assertions.assertThat(dto.bookmarkCount()).isEqualTo(1);
    }

}