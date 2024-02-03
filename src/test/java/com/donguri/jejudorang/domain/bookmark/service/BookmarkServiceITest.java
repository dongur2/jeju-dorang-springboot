package com.donguri.jejudorang.domain.bookmark.service;

import com.donguri.jejudorang.domain.bookmark.entity.Bookmark;
import com.donguri.jejudorang.domain.bookmark.repository.BookmarkRepository;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.service.CommunityService;
import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Password;
import com.donguri.jejudorang.domain.user.repository.RoleRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
class BookmarkServiceITest {

    @Autowired
    EntityManager em;

    @Autowired BookmarkService bookmarkService;
    @Autowired CommunityService communityService;

    @Autowired CommunityRepository communityRepository;
    @Autowired BookmarkRepository bookmarkRepository;
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;

    @AfterEach
    void after() {
        em.clear();
        bookmarkRepository.flush();
        communityRepository.flush();
        userRepository.flush();
    }

    @Test
    @Transactional
    void 유저_북마크_생성_테스트() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").build();
        Authentication authentication = Authentication.builder().user(user).email("user@mail.com").agreement(AgreeRange.ALL).build();
        Password password = Password.builder().user(user).password("12345678").build();

        Set<Role> testRoles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
        testRoles.add(userRole);

        user.updateRole(testRoles);
        user.updateProfile(profile);
        user.updateAuth(authentication);
        user.updatePwd(password);

        User user1 = User.builder().loginType(LoginType.BASIC).build();
        Profile profile1 = Profile.builder().user(user1).externalId("userId1").nickname("userNickname").build();
        Authentication authentication1 = Authentication.builder().user(user1).email("user1@mail.com").agreement(AgreeRange.ALL).build();
        Password password1 = Password.builder().user(user1).password("12345678").build();

        Set<Role> testRoles1 = new HashSet<>();
        Role userRole1 = roleRepository.findByName(ERole.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
        testRoles1.add(userRole1);

        user1.updateRole(testRoles1);
        user1.updateProfile(profile1);
        user1.updateAuth(authentication1);
        user1.updatePwd(password1);

        User savedUser = userRepository.save(user);
        User savedUser1 = userRepository.save(user1);

        Community testCommunity = Community.builder().title("제목").writer(savedUser).content("본문").build();
        testCommunity.setBoardType("party");
        testCommunity.setDefaultJoinState();

        Long saved = communityRepository.save(testCommunity).getId();

        // when
        Bookmark newBookmark = Bookmark.builder().user(savedUser1).community(communityRepository.findById(saved)
                        .orElseThrow(() -> new RuntimeException("글 없음")))
                .build();

        bookmarkRepository.save(newBookmark);

        // then
        Assertions.assertThat(bookmarkRepository.findByUserAndCommunityId(savedUser1,saved).get().getUser())
                .isEqualTo(savedUser1);
        Assertions.assertThat(bookmarkRepository.findByUserAndCommunityId(savedUser1,saved).get().getCommunity().getId())
                .isEqualTo(saved);
    }

//    @Test
//    void 북마크_삭제() {
//        //given
//        User testUser = userRepository.findById(1L).get();
//        Long existing = communityRepository.findById(4L).get().getId();
//
//        //when
//        bookmarkService.changeCommunityBookmarkState(testUser, existing);
//
//        //then
//        Community updated = communityRepository.findById(existing).get();
//        System.out.println(updated.getBookmarksCount());
//        Assertions.assertThat(updated.getBookmarksCount()).isEqualTo(0);
//    }
//
//    @Test
//    void 북마크_개수_테스트() {
//        //given
//        Community community = communityRepository.findById(7L).get();
//        //when
//        PartyDetailResponseDto dto = PartyDetailResponseDto.from(community, null);
//        //then
//        Assertions.assertThat(community.getBookmarksCount()).isEqualTo(1);
//        Assertions.assertThat(dto.bookmarkCount()).isEqualTo(1);
//    }

}