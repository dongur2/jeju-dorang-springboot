package com.donguri.jejudorang.domain.notification.service;

import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.comment.Comment;
import com.donguri.jejudorang.domain.community.entity.comment.IsDeleted;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.repository.comment.CommentRepository;
import com.donguri.jejudorang.domain.notification.repository.NotificationRepository;
import com.donguri.jejudorang.domain.notification.repository.SseEmitterRepository;
import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Authentication;
import com.donguri.jejudorang.domain.user.entity.auth.Password;
import com.donguri.jejudorang.domain.user.repository.RoleRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashSet;
import java.util.Set;


@SpringBootTest
class NotificationServiceITest {

    @Autowired EntityManager em;

    @Autowired RoleRepository roleRepository;
    @Autowired UserRepository userRepository;

    @Autowired NotificationService notificationService;
    @Autowired SseEmitterRepository sseEmitterRepository;
    @Autowired NotificationRepository notificationRepository;

    @Autowired CommunityRepository communityRepository;
    @Autowired CommentRepository commentRepository;

    @AfterEach
    void after() {
        em.clear();
        userRepository.flush();
        sseEmitterRepository.flush();
        notificationRepository.flush();
        communityRepository.flush();
        commentRepository.flush();
    }


    @Test
    void SseEmitter_Connection() {
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

        Long userId = userRepository.save(user).getId();

        // when
        sseEmitterRepository.save(userId, new SseEmitter(60 * 1000 * 60L));

        // then
        org.assertj.core.api.Assertions.assertThat(sseEmitterRepository).isNotNull();
        org.assertj.core.api.Assertions.assertThat(sseEmitterRepository.get(userId)).isPresent();
    }

    @Test
    void SseEmitter_send_notification() {
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

        User user1 = userRepository.save(user);

        // * sseEmitter instance created
        SseEmitter sseEmitter = sseEmitterRepository.save(user.getId(), new SseEmitter(60 * 1000 * 60L));

        Community newPost = Community.builder().writer(user1).title("test title1").content("test content1").build();
        newPost.setBoardType("party");
        newPost.setDefaultJoinState();

        Community savedPost = communityRepository.save(newPost);


        //when
        Comment comment1 = Comment.builder().community(savedPost).user(user1).content("test_comment1")
                .cmtDepth(0).isDeleted(IsDeleted.EXISTING).cmtOrder(0L).cmtGroup(0L).build();
        commentRepository.save(comment1); // new comment

        //then
        // * send notification
        Assertions.assertDoesNotThrow(() -> notificationService.sendNotification(user, savedPost.getTitle(), 0L));



    }


}