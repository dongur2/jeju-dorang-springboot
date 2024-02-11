package com.donguri.jejudorang.domain.community.service.comment;

import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequest;
import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequestWithId;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.comment.Comment;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.repository.comment.CommentRepository;
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
public class CommentServiceI implements CommentService{

    @Autowired private final JwtProvider jwtProvider;
    @Autowired private final UserRepository userRepository;
    @Autowired private final CommentRepository commentRepository;
    @Autowired private final CommunityRepository communityRepository;

    public CommentServiceI(JwtProvider jwtProvider, UserRepository userRepository, CommentRepository commentRepository, CommunityRepository communityRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.communityRepository = communityRepository;
    }


    @Override
    @Transactional
    public void writeNewComment(String accessToken, Long postId, CommentRequest newComment) {
        try {
            String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(accessToken);
            User nowUser = userRepository.findByExternalId(userNameFromJwtToken)
                    .orElseThrow(() -> new EntityNotFoundException("가입된 사용자가 아닙니다."));

            Community nowPost = communityRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException("존재하는 게시물이 아닙니다."));

            Comment savedComment = commentRepository.save(Comment.builder()
                    .community(nowPost)
                    .user(nowUser)
                    .content(newComment.content())
                    .build()
            );

            nowPost.addComment(savedComment);

        } catch (Exception e) {
            log.error("댓글 작성 실패: {}", e.getMessage());
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

}
