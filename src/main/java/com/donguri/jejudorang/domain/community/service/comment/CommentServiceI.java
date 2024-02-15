package com.donguri.jejudorang.domain.community.service.comment;

import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequest;
import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequestWithId;
import com.donguri.jejudorang.domain.community.dto.request.comment.ReCommentRequest;
import com.donguri.jejudorang.domain.community.dto.response.comment.CommentResponse;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.comment.Comment;
import com.donguri.jejudorang.domain.community.entity.comment.IsDeleted;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.repository.comment.CommentRepository;
import com.donguri.jejudorang.domain.notification.service.NotificationService;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import com.donguri.jejudorang.global.auth.jwt.JwtProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class CommentServiceI implements CommentService{

    // 댓글, 대댓글 통합한 순서 정렬 위한 인덱스
    private Long orderIdx = 0L;

    private Long notificationId = 0L;

    @Autowired private final JwtProvider jwtProvider;
    @Autowired private final UserRepository userRepository;
    @Autowired private final CommentRepository commentRepository;
    @Autowired private final CommunityRepository communityRepository;

    @Autowired private final NotificationService notificationService;

    public CommentServiceI(JwtProvider jwtProvider, UserRepository userRepository, CommentRepository commentRepository, CommunityRepository communityRepository, NotificationService notificationService) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.communityRepository = communityRepository;
        this.notificationService = notificationService;
    }


    @Override
    @Transactional
    public void writeNewComment(String accessToken, CommentRequest newComment) {
        try {
            String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(accessToken);
            User nowUser = userRepository.findByExternalId(userNameFromJwtToken)
                    .orElseThrow(() -> new EntityNotFoundException("가입된 사용자가 아닙니다."));

            Community nowPost = communityRepository.findById(newComment.postId())
                    .orElseThrow(() -> new EntityNotFoundException("존재하는 게시물이 아닙니다."));

            Comment savedComment = commentRepository.save(Comment.builder()
                    .community(nowPost)
                    .user(nowUser)
                    .content(newComment.content())
                    .cmtDepth(0)
                    .cmtOrder(orderIdx++)
                    .isDeleted(IsDeleted.EXISTING)
                    .build());
            savedComment.updateCmtGroup();

            nowPost.addComment(savedComment);

            // 새 댓글 알림 전송
            Optional<User> writer = Optional.ofNullable(nowPost.getWriter());
            writer.ifPresentOrElse(
                    postWriter -> notificationService.sendNotification(postWriter, nowPost.getTitle(), notificationId++),
                    () -> log.info("탈퇴한 회원의 글입니다.")
            );

        } catch (Exception e) {
            log.error("댓글 작성 실패: {}", e.getMessage());
            throw e;
        }
    }


    @Override
    @Transactional
    public void writeNewReComment(String accessToken, ReCommentRequest newReComment) {
        try {
            String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(accessToken);

            Community nowPost = communityRepository.findById(newReComment.postId())
                    .orElseThrow(() -> new EntityNotFoundException("해당하는 게시글이 없습니다."));

            User nowUser = userRepository.findByExternalId(userNameFromJwtToken)
                    .orElseThrow(() -> new EntityNotFoundException("해당하는 회원이 없습니다."));


            Comment savedReComment = commentRepository.save(Comment.builder()
                    .community(nowPost)
                    .user(nowUser)
                    .content(newReComment.content())
                    .cmtGroup(newReComment.cmtId())
                    .cmtDepth(1)
                    .cmtOrder(orderIdx++)
                    .isDeleted(IsDeleted.EXISTING)
                    .build());

            nowPost.addComment(savedReComment);

            // 새 댓글 알림 전송
            Optional<User> writer = Optional.ofNullable(nowPost.getWriter());
            writer.ifPresentOrElse(
                    postWriter -> notificationService.sendNotification(postWriter, nowPost.getTitle(), notificationId++),
                    () -> log.info("탈퇴한 회원의 글입니다.")
            );

        } catch (Exception e) {
            log.error("대댓글 작성 실패: {}", e.getMessage());
            throw e;
        }
    }



    @Override
    @Transactional
    public void modifyComment(String accessToken, CommentRequestWithId commentToUpdate) throws IllegalAccessException {
        try {
            String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(accessToken);

            Comment originalCmt = commentRepository.findById(commentToUpdate.cmtId())
                    .orElseThrow(() -> new EntityNotFoundException("해당하는 댓글이 없습니다."));

            // 로그인 유저가 댓글 작성자가 아닐 경우 예외 처리
            if(!userNameFromJwtToken.equals(originalCmt.getUser().getProfile().getExternalId())) {
                throw new IllegalAccessException("댓글 작성자 당사자만 댓글을 수정할 수 있습니다.");
            }

            // 댓글 내용 수정
            originalCmt.updateContent(commentToUpdate.content());

        } catch (Exception e) {
            log.error("댓글 수정 실패: {}", e.getMessage());
            throw e;
        }
    }


    @Override
    @Transactional
    public void deleteComment(String accessToken, Long cmtId) throws IllegalAccessException {
        try {
            String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(accessToken);

            Comment cmtToDelete = commentRepository.findById(cmtId)
                    .orElseThrow(() -> new EntityNotFoundException("해당하는 댓글이 없습니다."));

            // 로그인 유저가 댓글 작성자가 아닐 경우 예외 처리
            if(!userNameFromJwtToken.equals(cmtToDelete.getUser().getProfile().getExternalId())) {
                throw new IllegalAccessException("댓글 작성자 당사자만 댓글을 삭제할 수 있습니다.");
            }

            // 삭제 처리
            cmtToDelete.updateIsDeleted();

        } catch (Exception e) {
            throw e;
        }
    }


    @Override
    @Transactional
    public void findAllCmtsByUserAndSetWriterNull(Long writerId) {
        try {
            // 회원이 작성한 댓글이 존재할 경우, 작성자를 NULL 처리
            commentRepository.findAllByUserId(writerId)
                    .ifPresent(comments -> comments.forEach(Comment::deleteWriter));

        } catch (Exception e) {
            log.error("작성자 null 처리 실패: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public List<CommentResponse> findAllCmtsOnCommunity(Long communityId) {
        try {
            return commentRepository.findAllByCommunityIdOrderByCmtGroupAscCmtOrderAsc(communityId).stream()
                    .map(CommentResponse::from).toList();

        } catch (Exception e) {
            log.error("해당하는 게시글에 대한 댓글을 불러오지 못했습니다. {}", e.getMessage());
            throw e;
        }
    }


}
