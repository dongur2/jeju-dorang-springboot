package com.donguri.jejudorang.domain.community.service.comment;

import com.donguri.jejudorang.domain.bookmark.repository.CommunityBookmarkRepository;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.comment.Comment;
import com.donguri.jejudorang.domain.community.entity.comment.ReComment;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.repository.comment.CommentRepository;
import com.donguri.jejudorang.domain.community.repository.comment.ReCommentRepository;
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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;


@SpringBootTest
class ReCommentServiceITest {
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
    ReCommentRepository reCommentRepository;

    @Autowired
    CommunityBookmarkRepository bookmarkRepository;

    @AfterEach
    void after() {
        em.clear();
        reCommentRepository.flush();
        commentRepository.flush();
        communityRepository.flush();
        userRepository.flush();
        communityWithTagRepository.flush();
        tagRepository.flush();
        bookmarkRepository.flush();
    }


    @Test
    @Transactional
    void 대댓글_작성() {
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
        Long idx = 0L;

        Comment comment1 = Comment.builder()
                .community(testCommunity).user(savedUser1).content("테스트 댓글1").cmtDepth(0).build();
        comment1.updateCmtOrder(idx++);
        Comment savedComment1 = commentRepository.save(comment1);

        comment1.updateCmtGroup();

        savedCommunity.addComment(savedComment1); // 댓글 저장

        Comment reComment1 = Comment.builder()
                .community(testCommunity).user(savedUser).content("테스트 대댓글1-1").cmtDepth(1).cmtGroup(comment1.getId()).build();
        reComment1.updateCmtOrder(idx++);
        commentRepository.save(reComment1);

        savedCommunity.addComment(reComment1); // 대댓글 저장

        //then
        Assertions.assertThat(savedCommunity.getComments().size()).isEqualTo(2);
        Assertions.assertThat(savedCommunity.getComments().stream().filter(cmt -> cmt.getCmtGroup().equals(comment1.getId())).count()).isEqualTo(2);
        Assertions.assertThat(savedCommunity.getComments().stream().filter(cmt -> cmt.getCmtDepth() == 0).count()).isEqualTo(1);
        Assertions.assertThat(savedCommunity.getComments().stream().filter(cmt -> cmt.getCmtDepth() == 1).count()).isEqualTo(1);
    }

}