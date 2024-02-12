package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityDetailResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityForModifyResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityListResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityTypeResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.comment.CommentResponse;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.service.comment.CommentService;
import com.donguri.jejudorang.domain.community.service.tag.CommunityWithTagService;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import com.donguri.jejudorang.global.config.jwt.JwtProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CommunityServiceI implements CommunityService {

    @Autowired private final JwtProvider jwtProvider;

    @Autowired private final CommentService commentService;

    @Autowired private final UserRepository userRepository;
    @Autowired private final CommunityRepository communityRepository;
    @Autowired private final CommunityWithTagService communityWithTagService;

    public CommunityServiceI(JwtProvider jwtProvider, CommentService commentService, UserRepository userRepository, CommunityRepository communityRepository, CommunityWithTagService communityWithTagService) {
        this.jwtProvider = jwtProvider;
        this.commentService = commentService;
        this.userRepository = userRepository;
        this.communityRepository = communityRepository;
        this.communityWithTagService = communityWithTagService;
    }

    @Override
    @Transactional
    public CommunityTypeResponseDto saveNewPost(CommunityWriteRequestDto postToWrite, String token) {
        try {
            // 토큰에서 현재 작성자 추출
            String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(token);
            User writer = userRepository.findByExternalId(userNameFromJwtToken)
                    .orElseThrow(() -> new EntityNotFoundException("아이디에 해당하는 유저가 없습니다. " + userNameFromJwtToken));

            // 태그 제외 entity 생성 & 저장
            Community savedCommunity = communityRepository.save(postToWrite.toEntity(writer));

            // 작성된 태그가 존재할 경우에만 태그 저장 메서드 실행
            if (postToWrite.tags() != null) {
                log.info("태그 존재함");
                communityWithTagService.saveTagToPost(savedCommunity, postToWrite.tags());
            }
            
            log.info("게시글 작성 완료 : {} written by {}", savedCommunity.getTitle(),
                                                        savedCommunity.getWriter().getProfile().getExternalId());

            // 리다이렉트할 때 넣어줄 글타입
            return new CommunityTypeResponseDto(setTypeForRedirect(savedCommunity));

        } catch (Exception e) {
            log.error("게시글 작성에 실패했습니다 : {}", e.getMessage());
            throw e;
        }
    }
    

    @Override
    @Transactional
    public Map<String, Object> getCommunityPost(Long communityId, boolean forModify, HttpServletRequest request) {

        try {
            Map<String, Object> resMap = new HashMap<>();

            Community found = communityRepository.findById(communityId)
                    .orElseThrow(() -> new EntityNotFoundException("다음 ID에 해당하는 글을 찾을 수 없습니다: " + communityId));

            List<String> tagsToStringList = found.getTags().stream()
                    .map(communityWithTag -> communityWithTag.getTag().getKeyword())
                    .toList();

            // 댓글 따로 조회 후 리턴
            List<CommentResponse> cmtList = commentService.findAllCmtsOnCommunity(communityId);

            /* 수정폼으로 데이터 불러오기
            * */
            if (forModify) {
                resMap.put("result", CommunityForModifyResponseDto.from(found, tagsToStringList));
                return resMap;

            /* 상세글 조회
            * */
            } else {

                // 1. Access Token 쿠키가 존재하는 경우
                if(request.getCookies() != null
                        && Arrays.stream(request.getCookies())
                        .anyMatch(cookie -> cookie.getName().equals("access_token"))) {

                    log.info("액세스 쿠키가 존재합니다.");
                    Cookie accessToken = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("access_token")).findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다"));

                    // 1-1. Access Token이 유효한지 확인 -> catch: 유효하지 않은 경우
                    StringBuilder idFromJwt = new StringBuilder();
                    try {
                        idFromJwt.append(jwtProvider.getUserNameFromJwtToken(accessToken.getValue()));
                    } catch (Exception e) {
                        log.info("유효한 토큰이 아닙니다. 비회원은 북마크 여부를 확인할 수 없습니다.");
                        resMap.put("result", CommunityDetailResponseDto.from(found, tagsToStringList));
                        resMap.put("cmts", cmtList);
                        return resMap;
                    }

                    // 1-2. Access Token이 유효 -> from(.., idFromJwt) 북마크 여부 확인
                    resMap.put("post", CommunityDetailResponseDto.from(found, tagsToStringList, idFromJwt.toString()));
                    resMap.put("cmts", cmtList);
                    log.info("{}가 북마크한 글입니다. isBookmarked == {}", idFromJwt,
                            CommunityDetailResponseDto.from(found, tagsToStringList, idFromJwt.toString()).isBookmarked());

                // 2. Access Token 쿠키가 없는 경우
                } else {
                    log.info("비회원은 북마크 여부를 확인할 수 없습니다.");
                    resMap.put("post", CommunityDetailResponseDto.from(found, tagsToStringList));
                    resMap.put("cmts", cmtList);
                }

                return resMap;
            }

        } catch (Exception e) {
            log.error("게시글 불러오기를 실패했습니다. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public Page<CommunityListResponseDto> getAllPostsWrittenByUser(User writer, Pageable pageable) {
        return communityRepository.findAllByWriterId(writer.getId(), pageable)
                .map(CommunityListResponseDto::from);
    }

    @Override
    @Transactional
    public Page<CommunityListResponseDto> getAllPostsWithCommentsByUser(User writer, Pageable pageable) {
        return communityRepository.findAllByCommentWriterId(writer.getId(), pageable)
                .map(CommunityListResponseDto::from);
    }


    @Override
    @Transactional
    public CommunityTypeResponseDto updatePost(Long communityId, CommunityWriteRequestDto postToUpdate) {
        try {
            Community existingCommunity = communityRepository.findById(communityId)
                    .orElseThrow(() -> new EntityNotFoundException("다음 ID에 해당하는 글을 찾을 수 없습니다: " + communityId));

            // 제목, 글분류, 글내용, 모집상태 업데이트 (dirty checking)
            existingCommunity.update(postToUpdate);

            // 태그 업데이트
            if (postToUpdate.tags() != null) {
                communityWithTagService.saveTagToPost(existingCommunity, postToUpdate.tags());
            }

            // 리다이렉트할 때 넣어줄 글타입
            String typeForDto = setTypeForRedirect(existingCommunity);
            return new CommunityTypeResponseDto(typeForDto);

        } catch (Exception e) {
            log.error("게시글 업데이트 실패: {}", e.getMessage());
            throw e;
        }
    }


    @Override
    @Transactional
    public void updateView(Long communityId) {
        try {
            Community postToUpdate = communityRepository.findById(communityId)
                    .orElseThrow(() -> new EntityNotFoundException("해당하는 게시글이 없습니다."));

            postToUpdate.upViewCount();

        } catch (Exception e) {
            log.error("조회수 업데이트 실패: {}", e.getMessage());
            throw e;
        }
    }

    /*
    * 회원 탈퇴시 작성자 - 작성글 연관 관계 삭제
    * */
    @Override
    @Transactional
    public void findAllPostsByUserAndSetWriterNull(Long userId) {
        try {
            communityRepository.findAllByWriterId(userId).forEach(Community::deleteWriter);

        } catch (Exception e) {
            log.error("작성글 작성자 삭제 실패: {}", e.getMessage());
        }
    }

    /*
    * 커뮤니티 삭제
    * */
    @Override
    @Transactional
    public void deleteCommunityPost(String accessToken, Long communityId) {
        try {
            String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(accessToken);
            Community nowPost = communityRepository.findById(communityId)
                    .orElseThrow(() -> new EntityNotFoundException("해당하는 게시글이 없습니다."));

            if(!nowPost.getWriter().getProfile().getExternalId().equals(userNameFromJwtToken)) {
                throw new IllegalAccessException("게시글은 작성자만 삭제할 수 있습니다.");
            }

            communityRepository.delete(nowPost);

        } catch (IllegalAccessException e) {
            log.error("작성자가 아니면 삭제할 수 없습니다.");
            throw new RuntimeException(e);

        } catch (Exception e) {
            log.error("게시글 삭제 실패: {}", e.getMessage());
            throw e;
        }
    }

    private static String setTypeForRedirect(Community resultCommunity) {
        if (resultCommunity.getType() == BoardType.PARTY) {
            return "parties";
        } else  {
            return "chats";
        }
    }


}
