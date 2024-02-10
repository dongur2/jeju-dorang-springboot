package com.donguri.jejudorang.domain.community.repository;

import com.donguri.jejudorang.domain.bookmark.repository.CommunityBookmarkRepository;
import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.repository.tag.CommunityWithTagRepository;
import com.donguri.jejudorang.domain.community.repository.tag.TagRepository;
import com.donguri.jejudorang.domain.community.service.CommunityService;
import com.donguri.jejudorang.domain.community.service.tag.CommunityWithTagService;
import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Authentication;
import com.donguri.jejudorang.domain.user.entity.auth.Password;
import com.donguri.jejudorang.domain.user.repository.RoleRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
class CommunityRepositoryTest {
    @Autowired EntityManager em;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired CommunityRepository communityRepository;
    @Autowired
    CommunityService communityService;

    @Autowired
    CommunityWithTagService communityWithTagService;
    @Autowired
    CommunityWithTagRepository communityWithTagRepository;
    @Autowired
    TagRepository tagRepository;

    @Autowired
    CommunityBookmarkRepository bookmarkRepository;

    @AfterEach
    void after() {
        em.clear();
        userRepository.flush();
        communityRepository.flush();
        communityWithTagRepository.flush();
        tagRepository.flush();
        bookmarkRepository.flush();
    }

    @Test
    void 작성자_아이디로_작성글_모두_불러오기() {
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
        CommunityWriteRequestDto postToWrite1 = CommunityWriteRequestDto.builder()
                .title("커뮤니티 글작성 테스트 제목1")
                .type("chat")
                .content("커뮤니티 글작성 테스트 - CHAT")
                .build();

        CommunityWriteRequestDto postToWrite2 = CommunityWriteRequestDto.builder()
                .title("커뮤니티 글작성 테스트 제목2")
                .type("chat")
                .content("커뮤니티 글작성 테스트 - CHAT")
                .build();

        communityRepository.save(postToWrite1.toEntity(foundUser));
        communityRepository.save(postToWrite2.toEntity(foundUser));

        //then
        org.assertj.core.api.Assertions.assertThat(communityRepository.findAllByWriterId(foundUser.getId()).size()).isEqualTo(2);
    }

}