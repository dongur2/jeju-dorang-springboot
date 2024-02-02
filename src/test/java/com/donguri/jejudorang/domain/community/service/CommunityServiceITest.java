package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityForModifyResponseDto;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.user.dto.SignUpRequest;
import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Password;
import com.donguri.jejudorang.domain.user.repository.RoleRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
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

    @AfterEach
    void after() {
        em.clear();
        communityRepository.flush();
        userRepository.flush();
    }

    @Test
    void 커뮤니티_글작성_성공() {
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
        CommunityWriteRequestDto postToWrite = CommunityWriteRequestDto.builder()
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
        CommunityWriteRequestDto postToWrite = CommunityWriteRequestDto.builder()
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



//
//    @Test
//    void 글작성_태그_길이_테스트() {
//
//        // given
//        CommunityWriteRequestDto writeDto =
//                new CommunityWriteRequestDto("제목5", "parties", "내용5", "일이삼사오육칠팔구십일이삼사오육칠팔구십");
//        // when
//        communityService.saveNewPost(writeDto);
//
//        // then
//        CommunityForModifyResponseDto savedDto = communityService.getCommunityPost(6L);
//        Assertions.assertThat(savedDto.title()).isEqualTo(writeDto.title());
//
//        savedDto.tags().forEach(tag -> System.out.println("tag = " + tag));
//
//    }
//    @Test
//    void 글작성_태그_길이_예외_처리_테스트() {
//
//        // given
//        CommunityWriteRequestDto writeDto =
//                new CommunityWriteRequestDto("제목5", "parties", "내용5", "일이삼사오육칠팔구십일이삼사오육칠팔구십뿅");
//        // when
//        try {
//            communityService.saveNewPost(writeDto);
//            // then
//            CommunityForModifyResponseDto savedDto = communityService.getCommunityPost(6L);
//            Assertions.assertThat(savedDto.title()).isEqualTo(writeDto.title());
//
//            savedDto.tags().forEach(tag -> System.out.println("tag = " + tag));
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//    }

}