package com.donguri.jejudorang.domain.community.service.comment;

import com.donguri.jejudorang.domain.community.dto.request.comment.ReCommentRequest;
import com.donguri.jejudorang.domain.community.dto.request.comment.ReCommentRequestWIthId;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.comment.Comment;
import com.donguri.jejudorang.domain.community.entity.comment.IsDeleted;
import com.donguri.jejudorang.domain.community.entity.comment.ReComment;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.repository.comment.CommentRepository;
import com.donguri.jejudorang.domain.community.repository.comment.ReCommentRepository;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import com.donguri.jejudorang.global.config.jwt.JwtProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ReCommentServiceI implements ReCommentService{

    private Long idx = 0L;

    @Autowired private final JwtProvider jwtProvider;
    @Autowired private final ReCommentRepository reCommentRepository;

    @Autowired private final UserRepository userRepository;
    @Autowired private final CommunityRepository communityRepository;
    @Autowired private final CommentRepository commentRepository;

    public ReCommentServiceI(JwtProvider jwtProvider, ReCommentRepository reCommentRepository, UserRepository userRepository, CommunityRepository communityRepository, CommentRepository commentRepository) {
        this.jwtProvider = jwtProvider;
        this.reCommentRepository = reCommentRepository;
        this.userRepository = userRepository;
        this.communityRepository = communityRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public void writeNewReComment(String accessToken, ReCommentRequest newReComment) {
        try {
            String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(accessToken);

            Community community = communityRepository.findById(newReComment.postId())
                    .orElseThrow(() -> new EntityNotFoundException("해당하는 게시글이 없습니다."));

            User nowUser = userRepository.findByExternalId(userNameFromJwtToken)
                    .orElseThrow(() -> new EntityNotFoundException("해당하는 회원이 없습니다."));


            Comment savedReComment = commentRepository.save(Comment.builder()
                    .community(community)
                    .user(nowUser)
                    .content(newReComment.content())
                    .cmtGroup(newReComment.cmtId())
                    .cmtDepth(1)
                    .isDeleted(IsDeleted.EXISTING)
                    .build());
            savedReComment.updateCmtOrder(idx++);

            community.addComment(savedReComment);

        } catch (Exception e) {
            log.error("대댓글 작성 실패: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public void modifyReComment(String accessToken, ReCommentRequestWIthId reCommentToUpdate) throws IllegalAccessException {
        try {
            String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(accessToken);

            Comment originalReCmt = commentRepository.findById(reCommentToUpdate.cmtId())
                    .orElseThrow(() -> new EntityNotFoundException("해당하는 대댓글이 없습니다."));

            // 로그인 유저가 대댓글 작성자가 아닐 경우 예외 처리
            if(!userNameFromJwtToken.equals(originalReCmt.getUser().getProfile().getExternalId())) {
                throw new IllegalAccessException("대댓글 작성자 당사자만 대댓글을 수정할 수 있습니다.");
            }

            originalReCmt.updateContent(reCommentToUpdate.content());

        } catch (Exception e) {
            log.error("대댓글 수정 실패: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteReComment(String accessToken, Long cmtId) throws IllegalAccessException {
        try {
            String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(accessToken);

            Comment reCmtToDelete = commentRepository.findById(cmtId)
                    .orElseThrow(() -> new EntityNotFoundException("해당하는 대댓글이 없습니다."));

            // 로그인 유저가 대댓글 작성자가 아닐 경우 예외 처리
            if(!userNameFromJwtToken.equals(reCmtToDelete.getUser().getProfile().getExternalId())) {
                throw new IllegalAccessException("대댓글 작성자 당사자만 대댓글을 삭제할 수 있습니다.");
            }

            // 삭제 처리
            reCmtToDelete.updateIsDeleted();

        } catch (Exception e) {
            throw e;
        }
    }


}
