package com.donguri.jejudorang.domain.community.service.comment;

import com.donguri.jejudorang.domain.bookmark.repository.CommunityBookmarkRepository;
import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequest;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.comment.Comment;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.repository.comment.CommentRepository;
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
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@SpringBootTest
class CommentServiceITest {
    @Autowired
    EntityManager em;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    CommunityRepository communityRepository;
    @Autowired
    CommunityService communityService;

    @Autowired
    CommunityWithTagService communityWithTagService;
    @Autowired
    CommunityWithTagRepository communityWithTagRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    CommentRepository commentRepository;

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
    @Transactional
    void 댓글_작성_성공() {
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

        Community savedCommunity = communityRepository.save(testCommunity);

        //when
        Comment comment1 = Comment.builder().community(testCommunity).user(savedUser1).content("test_comment1").build();
        Comment comment2 = Comment.builder().community(testCommunity).user(savedUser1).content("test_comment2").build();
        Comment comment3 = Comment.builder().community(testCommunity).user(savedUser1).content("test_comment3").build();
        Comment savedComment1 = commentRepository.save(comment1);
        Comment savedComment2 = commentRepository.save(comment2);
        Comment savedComment3 = commentRepository.save(comment3);
        savedCommunity.addComment(savedComment1);
        savedCommunity.addComment(savedComment2);
        savedCommunity.addComment(savedComment3);

        //then
        Assertions.assertThat(testCommunity.getComments().size()).isEqualTo(3);
    }

    @Test
    @Transactional
    void 댓글_수정() {
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

        Community savedCommunity = communityRepository.save(testCommunity);

        //when
        Comment comment1 = Comment.builder().community(testCommunity).user(savedUser1).content("test_comment1").build();
        Comment savedComment1 = commentRepository.save(comment1);
        savedCommunity.addComment(savedComment1);

        savedComment1.updateContent("댓글 수정");

        //then
        Assertions.assertThat(savedCommunity.getComments().get(0).getContent()).isEqualTo("댓글 수정");

    }

    @Test
    @Transactional
    void 댓글_삭제() {
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

        Community savedCommunity = communityRepository.save(testCommunity);

        //when
        Comment comment1 = Comment.builder().community(testCommunity).user(savedUser1).content("test_comment1").build();
        Comment savedComment1 = commentRepository.save(comment1);
        Long cmtId = savedComment1.getId();

        savedCommunity.addComment(savedComment1);

        savedComment1.getCommunity().deleteComment(savedComment1);
        commentRepository.delete(savedComment1);

        //then
        Assertions.assertThat(savedCommunity.getComments().size()).isEqualTo(0);
        Assertions.assertThat(commentRepository.findById(cmtId)).isEmpty();
    }

    @Test
    @Transactional
    void 작성자_탈퇴시_null() {
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

        Community savedCommunity = communityRepository.save(testCommunity);

        //when
        Comment comment1 = Comment.builder().community(testCommunity).user(savedUser1).content("test_comment1").build();
        Comment savedComment1 = commentRepository.save(comment1);
        Long cmtId = savedComment1.getId();

        savedCommunity.addComment(savedComment1);

        List<Comment> comments = commentRepository.findAllByUserId(savedUser1.getId()).orElseThrow(() -> new EntityNotFoundException("댓글 없음"));
        comments.forEach(cmt -> {
                    System.out.println("댓글 : "+ cmt.getContent() + ", writer: " + cmt.getUser().getProfile().getNickname());
                    cmt.deleteWriter();
                    System.out.println("댓글 작성자: " + cmt.getUser());
                });


        //then
        Assertions.assertThat(comment1.getUser()).isNull();
    }

}