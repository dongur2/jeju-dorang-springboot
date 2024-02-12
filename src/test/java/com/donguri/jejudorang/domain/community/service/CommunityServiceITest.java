package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.bookmark.entity.CommunityBookmark;
import com.donguri.jejudorang.domain.bookmark.repository.CommunityBookmarkRepository;
import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequest;
import com.donguri.jejudorang.domain.community.dto.response.CommunityDetailResponse;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.tag.CommunityWithTag;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.repository.tag.CommunityWithTagRepository;
import com.donguri.jejudorang.domain.community.repository.tag.TagRepository;
import com.donguri.jejudorang.domain.community.service.tag.CommunityWithTagService;
import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Authentication;
import com.donguri.jejudorang.domain.user.entity.auth.Password;
import com.donguri.jejudorang.domain.user.repository.RoleRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@SpringBootTest
class CommunityServiceITest {

    @Autowired EntityManager em;

    @Autowired RoleRepository roleRepository;
    @Autowired UserRepository userRepository;

    @Autowired CommunityRepository communityRepository;
    @Autowired CommunityService communityService;

    @Autowired CommunityWithTagService communityWithTagService;
    @Autowired CommunityWithTagRepository communityWithTagRepository;
    @Autowired TagRepository tagRepository;

    @Autowired
    CommunityBookmarkRepository bookmarkRepository;

    @AfterEach
    void after() {
        em.clear();
        communityRepository.flush();
        userRepository.flush();
        communityWithTagRepository.flush();
        tagRepository.flush();
        bookmarkRepository.flush();
    }

    @Test
    void 커뮤니티_글작성_태그_제외_성공() {
        // given
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

        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new RuntimeException("저장된 회원이 없습니다."));

        //when
        CommunityWriteRequest postToWrite = CommunityWriteRequest.builder()
                .title("커뮤니티 글작성 테스트 제목")
                .type("chat")
                .content("커뮤니티 글작성 테스트 - CHAT")
                .build();

        Community savedCommunity = communityRepository.save(postToWrite.toEntity(foundUser));

        //then
        System.out.println("게시글 작성 완료; 제목: "+savedCommunity.getTitle()+", 작성자: "+savedCommunity.getWriter().getProfile().getExternalId());
        Assertions.assertThat(savedCommunity.getWriter()).isEqualTo(foundUser);
    }

    @Test
    @Transactional
    void 커뮤니티_글작성_태그_포함_성공() {
        // given
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

        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new RuntimeException("저장된 회원이 없습니다."));

        //when
        CommunityWriteRequest postToWrite = CommunityWriteRequest.builder()
                .title("커뮤니티 글작성 테스트 제목")
                .type("chat")
                .content("커뮤니티 글작성 테스트 - CHAT")
                .tags("태그,테스트,CHAT")
                .build();

        Community savedCommunity = communityRepository.save(postToWrite.toEntity(foundUser));

        if (postToWrite.tags() != null) {
            communityWithTagService.saveTagToPost(savedCommunity, postToWrite.tags());
        }

        //then
        Assertions.assertThat(savedCommunity.getWriter()).isEqualTo(foundUser);
        List<Optional<CommunityWithTag>> tagList = communityWithTagRepository.findByCommunity(savedCommunity);

        List<String> keywords = new ArrayList<>();
        tagList.forEach(op ->
        {
            keywords.add(op.orElseThrow(
                            () -> new RuntimeException("태그 없음"))
                    .getTag().getKeyword());
        });

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                keywords.containsAll(Arrays.stream(postToWrite.tags().split(",")).toList())
        );
    }

    @Test
    @Transactional // JPA dirty checking
    void 유저_삭제할_경우_커뮤니티_작성자_NULL() {
        // given
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

        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new RuntimeException("저장된 회원이 없습니다."));

        //when
        CommunityWriteRequest postToWrite = CommunityWriteRequest.builder()
                .title("커뮤니티 글작성 테스트 제목")
                .type("chat")
                .content("커뮤니티 글작성 테스트 - CHAT")
                .build();

        Community savedCommunity = communityRepository.save(postToWrite.toEntity(foundUser));

        savedCommunity.deleteWriter();
        userRepository.delete(foundUser);

        //then
        Assertions.assertThat(communityRepository.findById(savedCommunity.getId())
                .orElseThrow(() -> new EntityNotFoundException("커뮤니티글 없음"))
                .getWriter()).isNull();

        Assertions.assertThat(communityRepository.findById(savedCommunity.getId())).isPresent();
    }


    @Test
    void 커뮤니티_글작성_실패_태그_길이_초과() {
        // given
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

        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new RuntimeException("저장된 회원이 없습니다."));

        //when
        CommunityWriteRequest postToWrite = CommunityWriteRequest.builder()
                .title("커뮤니티 글작성 테스트 제목")
                .type("chat")
                .content("커뮤니티 글작성 테스트 - CHAT")
                .tags("태그,테스트,일이삼사오륙칠팔구십일이삼사오륙칠팔구십일이삼사오륙칠팔구십")
                .build();

        Community savedCommunity = communityRepository.save(postToWrite.toEntity(foundUser));

        //then
        org.junit.jupiter.api.Assertions.assertThrows(ValidationException.class,
                () -> communityWithTagService.saveTagToPost(savedCommunity, postToWrite.tags()));
    }

    @Test
    @Transactional
    void 상세글_조회_북마크_여부_확인_로그인한_경우_북마크_함() {
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

        User savedUser = userRepository.save(user); // 게시글 작성자
        User savedUser1 = userRepository.save(user1); // 로그인한 유저

        Community testCommunity = Community.builder().title("제목").writer(savedUser).content("본문").build();
        testCommunity.setBoardType("party");
        testCommunity.setDefaultJoinState();

        Long saved = communityRepository.save(testCommunity).getId();

        // when
        Community savedCommunity = communityRepository.findById(saved)
                .orElseThrow(() -> new EntityNotFoundException("게시글 없음"));

        List<String> tags = null;
        if(savedCommunity.getTags() != null) {
             tags = savedCommunity.getTags().stream()
                    .map(cwt -> cwt.getTag().getKeyword())
                    .toList();
        }

        CommunityBookmark newBookmark = CommunityBookmark.builder()
                .user(savedUser1).community(savedCommunity)
                .build();
        CommunityBookmark savedBookmark = bookmarkRepository.save(newBookmark);
        savedCommunity.updateBookmarks(savedBookmark);

        CommunityDetailResponse dtoToReturn = CommunityDetailResponse.from(savedCommunity, tags, savedUser1.getProfile().getExternalId());

        //then
        Assertions.assertThat(dtoToReturn.isBookmarked()).isTrue();
    }

    @Test
    @Transactional
    void 상세글_조회_북마크_여부_확인_로그인한_경우_북마크_안함() {
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

        User savedUser = userRepository.save(user); // 게시글 작성자
        User savedUser1 = userRepository.save(user1); // 로그인한 유저

        Community testCommunity = Community.builder().title("제목").writer(savedUser).content("본문").build();
        testCommunity.setBoardType("party");
        testCommunity.setDefaultJoinState();

        Long saved = communityRepository.save(testCommunity).getId();

        // when
        Community savedCommunity = communityRepository.findById(saved)
                .orElseThrow(() -> new EntityNotFoundException("게시글 없음"));

        List<String> tags = null;
        if(savedCommunity.getTags() != null) {
            tags = savedCommunity.getTags().stream()
                    .map(cwt -> cwt.getTag().getKeyword())
                    .toList();
        }

        CommunityDetailResponse dtoToReturn = CommunityDetailResponse.from(savedCommunity, tags, savedUser1.getProfile().getExternalId());

        //then
        Assertions.assertThat(dtoToReturn.isBookmarked()).isFalse();
    }

    @Test
    @Transactional
    void 상세글_조회_북마크_여부_확인_비회원() {
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

        User savedUser = userRepository.save(user); // 게시글 작성자

        Community testCommunity = Community.builder().title("제목").writer(savedUser).content("본문").build();
        testCommunity.setBoardType("party");
        testCommunity.setDefaultJoinState();

        Long saved = communityRepository.save(testCommunity).getId();

        // when
        Community savedCommunity = communityRepository.findById(saved)
                .orElseThrow(() -> new EntityNotFoundException("게시글 없음"));

        List<String> tags = null;
        if(savedCommunity.getTags() != null) {
            tags = savedCommunity.getTags().stream()
                    .map(cwt -> cwt.getTag().getKeyword())
                    .toList();
        }

        CommunityDetailResponse dtoToReturn = CommunityDetailResponse.from(savedCommunity, tags);

        //then
        Assertions.assertThat(dtoToReturn.isBookmarked()).isFalse();
    }



}