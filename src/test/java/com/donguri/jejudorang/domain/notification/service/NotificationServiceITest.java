package com.donguri.jejudorang.domain.notification.service;

import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.comment.Comment;
import com.donguri.jejudorang.domain.community.entity.comment.IsDeleted;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.repository.comment.CommentRepository;
import com.donguri.jejudorang.domain.community.service.CommunityService;
import com.donguri.jejudorang.domain.community.service.comment.CommentService;
import com.donguri.jejudorang.domain.notification.dto.NotificationResponse;
import com.donguri.jejudorang.domain.notification.entity.Notification;
import com.donguri.jejudorang.domain.notification.repository.NotificationRepository;
import com.donguri.jejudorang.domain.notification.repository.SseEmitterRepository;
import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Authentication;
import com.donguri.jejudorang.domain.user.entity.auth.Password;
import com.donguri.jejudorang.domain.user.repository.RoleRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import com.donguri.jejudorang.domain.user.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
class NotificationServiceITest {

    @Autowired EntityManager em;

    @Autowired CommunityService communityService;
    @Autowired CommentService commentService;

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
        Assertions.assertDoesNotThrow(() -> notificationService.sendNotification(user, savedPost, 0L, comment1.getCmtDepth()));
    }

    @Test
    void SseEmitter_get_notification() {
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
        notificationService.sendNotification(user, savedPost, 0L, comment1.getCmtDepth());

        Comment comment2 = Comment.builder().community(savedPost).user(user1).content("test_comment2")
                .cmtDepth(0).isDeleted(IsDeleted.EXISTING).cmtOrder(0L).cmtGroup(1L).build();
        commentRepository.save(comment2); // new comment
        notificationService.sendNotification(user, savedPost, 1L, comment2.getCmtDepth());

        Comment comment3 = Comment.builder().community(savedPost).user(user1).content("test_comment3")
                .cmtDepth(0).isDeleted(IsDeleted.EXISTING).cmtOrder(0L).cmtGroup(2L).build();
        notificationService.sendNotification(user, savedPost, 2L, comment3.getCmtDepth());
        commentRepository.save(comment3); // new comment

        Comment comment4 = Comment.builder().community(savedPost).user(user1).content("test_comment4")
                .cmtDepth(0).isDeleted(IsDeleted.EXISTING).cmtOrder(0L).cmtGroup(3L).build();
        notificationService.sendNotification(user, savedPost, 3L, comment4.getCmtDepth());
        commentRepository.save(comment4); // new comment


        //then
        Assertions.assertDoesNotThrow(() ->
        {
            List<Notification> notifications = notificationRepository.findAllByOwnerId(user1.getId()).get();
            org.assertj.core.api.Assertions.assertThat(notifications.size()).isEqualTo(4);
        });
    }

    @Test
    void SseEmitter_get_notification_NO_CONTENT() {
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

        //when, then
        Assertions.assertThrows(NullPointerException.class,
            () -> {
                List<Notification> notiList = notificationRepository.findAllByOwnerId(user1.getId()).get();
                if(notiList.isEmpty()) {
                    throw new NullPointerException("새 알림이 없습니다.");
                }
            }
        );
    }

    @Test
    void SseEmitter_DELETE_notification() {
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

        notificationService.sendNotification(user, savedPost, 0L, comment1.getCmtDepth()); //send notification
        Notification notification = notificationRepository.findAllByOwnerId(user1.getId())
                .orElseThrow(() -> new EntityNotFoundException("알림 없음"))
                .get(0);

        notificationRepository.delete(notification);

        //then
        org.assertj.core.api.Assertions.assertThat(notificationRepository.findAllByOwnerId(user1.getId()).get().size()).isEqualTo(0);
    }

    @Test
    @Transactional
    void 회원_탈퇴시_알림_삭제() {
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
        notificationService.sendNotification(user, savedPost, 0L, comment1.getCmtDepth());


        Long user1Id = user1.getId();
        communityService.findAllPostsByUserAndSetWriterNull(user1Id);
        commentService.findAllCmtsByUserAndSetWriterNull(user1Id);

        notificationRepository.deleteAllByOwnerId(user1Id);

        userRepository.deleteById(user1Id);

        //then
        Optional<List<Notification>> allByOwnerId = notificationRepository.findAllByOwnerId(user1Id);
        org.assertj.core.api.Assertions.assertThat(allByOwnerId.get().size()).isEqualTo(0);

    }

}