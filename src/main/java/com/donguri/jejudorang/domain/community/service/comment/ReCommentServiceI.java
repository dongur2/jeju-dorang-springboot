package com.donguri.jejudorang.domain.community.service.comment;

import com.donguri.jejudorang.domain.community.dto.request.comment.ReCommentRequest;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.comment.Comment;
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

            Comment comment = commentRepository.findById(newReComment.cmtId())
                    .orElseThrow(() -> new EntityNotFoundException("해당하는 댓글이 없습니다."));

            User user = userRepository.findByExternalId(userNameFromJwtToken)
                    .orElseThrow(() -> new EntityNotFoundException("해당하는 회원이 없습니다."));

            ReComment reCommentToSave = ReComment.builder()
                    .community(community)
                    .comment(comment)
                    .user(user)
                    .content(newReComment.content())
                    .build();

            comment.addReComment(reCommentToSave);
            reCommentRepository.save(reCommentToSave);

        } catch (Exception e) {
            log.error("대댓글 작성 실패: {}", e.getMessage());
            throw e;
        }
    }

}
