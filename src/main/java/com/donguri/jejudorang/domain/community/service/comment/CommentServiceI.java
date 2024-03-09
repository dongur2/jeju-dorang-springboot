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
import com.donguri.jejudorang.domain.notification.entity.NotifyType;
import com.donguri.jejudorang.domain.notification.service.NotificationService;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import com.donguri.jejudorang.global.auth.jwt.JwtProvider;
import com.donguri.jejudorang.global.error.CustomErrorCode;
import com.donguri.jejudorang.global.error.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Service
public class CommentServiceI implements CommentService{

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
                    .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

            Community nowPost = communityRepository.findById(newComment.postId())
                    .orElseThrow(() -> new CustomException(CustomErrorCode.COMMUNITY_NOT_FOUND));

            Comment savedComment = commentRepository.save(Comment.builder()
                    .community(nowPost)
                    .user(nowUser)
                    .content(newComment.content())
                    .cmtDepth(0)
                    .isDeleted(IsDeleted.EXISTING)
                    .build());
            savedComment.updateCmtGroup();
            savedComment.updateCmtOrder();

            nowPost.addComment(savedComment);

            // 알림 전송
            sendNotificationToPostWriter(nowPost, savedComment);

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
                    .orElseThrow(() -> new CustomException(CustomErrorCode.COMMUNITY_NOT_FOUND));

            User nowUser = userRepository.findByExternalId(userNameFromJwtToken)
                    .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));


            Comment savedReComment = commentRepository.save(Comment.builder()
                    .community(nowPost)
                    .user(nowUser)
                    .content(newReComment.content())
                    .cmtGroup(newReComment.cmtId())
                    .cmtDepth(1)
                    .isDeleted(IsDeleted.EXISTING)
                    .build());

            savedReComment.updateCmtOrder();
            nowPost.addComment(savedReComment);

            // 알림 전송
            sendNotificationToPostWriter(nowPost, savedReComment);
            sendNotificationToCmtWriter(newReComment, savedReComment, nowPost);

        } catch (Exception e) {
            log.error("대댓글 작성 실패: {}", e.getMessage());
            throw e;
        }
    }

    private void sendNotificationToPostWriter(Community nowPost, Comment savedComment) {
        // * 글 작성자가 없거나 글 작성자와 새로운 댓글 작성자가 동일할 경우, 글 작성자와 대댓글의 원댓글 작성자가 동일할 경우 알림 전송하지 않음
        Optional<User> writer = Optional.ofNullable(nowPost.getWriter());
        Optional<User> headCmtWriter = Optional.ofNullable(
                Objects.requireNonNull(commentRepository.findById(savedComment.getCmtGroup())
                                .orElseGet(() -> {
                                    log.info("댓글이 존재하지 않습니다.");
                                    return null;}))
                        .getUser());


        if((writer.isPresent() && !writer.get().equals(savedComment.getUser())) // 글 작성자 != 새댓글 작성자
            && (headCmtWriter.isPresent() && !writer.get().equals(headCmtWriter.get()))) { // 글 작성자 != 원댓글 작성자
            // 새 댓글 알림 전송
            notificationService.sendNotification(writer.get(), nowPost, notificationId++, NotifyType.COMMENT);
        } else {
            log.info("알림을 전송할 수 없는 대상입니다.");
        }
    }

    private void sendNotificationToCmtWriter(ReCommentRequest newReComment, Comment savedReComment, Community nowPost) {
        Optional<User> cmtWriter  = Optional.ofNullable(commentRepository.findById(newReComment.cmtId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND))
                .getUser());

        // * 대댓글 작성자와 댓글 작성자가 동일할 경우 알림 전송하지 않음
        // 새 대댓글 알림 전송 -> 댓글 작성자에게
        if(cmtWriter.isPresent() && !cmtWriter.get().equals(savedReComment.getUser())) {
            notificationService.sendNotification(cmtWriter.get(), nowPost, notificationId++, NotifyType.RECOMMENT);
        }
    }



    @Override
    @Transactional
    public void modifyComment(String accessToken, CommentRequestWithId commentToUpdate) {
        try {
            String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(accessToken);

            Comment originalCmt = commentRepository.findById(commentToUpdate.cmtId())
                    .orElseThrow(() -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND));

            // 로그인 유저가 댓글 작성자가 아닐 경우 예외 처리
            if(!userNameFromJwtToken.equals(originalCmt.getUser().getProfile().getExternalId())) {
                throw new CustomException(CustomErrorCode.PERMISSION_ERROR);
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
    public void deleteComment(String accessToken, Long cmtId) {
        try {
            String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(accessToken);

            Comment cmtToDelete = commentRepository.findById(cmtId)
                    .orElseThrow(() -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND));

            // 로그인 유저가 댓글 작성자가 아닐 경우 예외 처리
            if(!userNameFromJwtToken.equals(cmtToDelete.getUser().getProfile().getExternalId())) {
                throw new CustomException(CustomErrorCode.PERMISSION_ERROR);
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
