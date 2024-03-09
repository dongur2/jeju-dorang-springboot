package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.bookmark.entity.CommunityBookmark;
import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequest;
import com.donguri.jejudorang.domain.community.dto.response.CommunityDetailResponse;
import com.donguri.jejudorang.domain.community.dto.response.CommunityForModifyResponse;
import com.donguri.jejudorang.domain.community.dto.response.CommunityMyPageListResponse;
import com.donguri.jejudorang.domain.community.dto.response.CommunityTypeResponse;
import com.donguri.jejudorang.domain.community.dto.response.comment.CommentResponse;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.comment.IsDeleted;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.service.comment.CommentService;
import com.donguri.jejudorang.domain.community.service.tag.CommunityWithTagService;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import com.donguri.jejudorang.global.auth.jwt.JwtProvider;
import com.donguri.jejudorang.global.error.CustomErrorCode;
import com.donguri.jejudorang.global.error.CustomException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    public CommunityTypeResponse saveNewPost(CommunityWriteRequest postToWrite, String token) {
        try {
            // 토큰에서 현재 작성자 추출
            String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(token);
            User writer = userRepository.findByExternalId(userNameFromJwtToken)
                    .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

            // 태그 제외 entity 생성 & 저장
            Community savedCommunity = communityRepository.save(postToWrite.toEntity(writer));

            // 작성된 태그가 존재할 경우에만 태그 저장 메서드 실행
            if (postToWrite.tags() != null) {
                communityWithTagService.saveTagToPost(savedCommunity, postToWrite.tags());
            }
            
            // 리다이렉트할 때 넣어줄 글타입
            return new CommunityTypeResponse(setTypeForRedirect(savedCommunity));

        } catch (Exception e) {
            throw e;
        }
    }
    

    @Override
    @Transactional
    public Map<String, Object> getCommunityPost(Long communityId, boolean forModify, HttpServletRequest request) {

        try {
            Map<String, Object> resMap = new HashMap<>();

            Community found = communityRepository.findById(communityId)
                    .orElseThrow(() -> new CustomException(CustomErrorCode.COMMUNITY_NOT_FOUND));

            List<String> tagsToStringList = found.getTags().stream()
                    .map(communityWithTag -> communityWithTag.getTag().getKeyword())
                    .toList();

            // 댓글 따로 조회 후 리턴
            List<CommentResponse> cmtList = commentService.findAllCmtsOnCommunity(communityId);

            /* 수정폼으로 데이터 불러오기
            * */
            if (forModify) {
                resMap.put("result", CommunityForModifyResponse.from(found, tagsToStringList));
                return resMap;

            /* 상세글 조회
            * */
            } else {

                Optional<Cookie[]> cookies = Optional.ofNullable(request.getCookies());

                // 1. Access Token 쿠키가 존재하는 경우
                if(cookies.isPresent() && Arrays.stream(cookies.get()).anyMatch(cookie -> cookie.getName().equals("access_token"))) {

                    Cookie accessToken = Arrays.stream(cookies.get()).filter(cookie -> cookie.getName().equals("access_token")).findFirst().get();

                    // 1-1. Access Token이 유효한지 확인 -> catch: 유효하지 않은 경우 비회원이므로 북마크 여부 확인할 수 없음
                    StringBuilder idFromJwt = new StringBuilder();
                    try {
                        idFromJwt.append(jwtProvider.getUserNameFromJwtToken(accessToken.getValue()));
                    } catch (Exception e) {
                        resMap.put("result", CommunityDetailResponse.from(found, tagsToStringList, null));
                        resMap.put("cmts", cmtList);
                        return resMap;
                    }

                    // 1-2. Access Token이 유효 -> from(.., idFromJwt) 북마크 여부 확인
                    resMap.put("post", CommunityDetailResponse.from(found, tagsToStringList, idFromJwt.toString()));
                    resMap.put("cmts", cmtList);

                // 2. Access Token 쿠키가 없는 경우: 비회원
                } else {
                    resMap.put("post", CommunityDetailResponse.from(found, tagsToStringList, null));
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
    public Page<CommunityMyPageListResponse> getAllPostsWrittenByUser(User writer, Pageable pageable) {
        return communityRepository.findAllByWriterId(writer.getId(), pageable)
                .map(CommunityMyPageListResponse::from);
    }

    @Override
    @Transactional
    public Page<CommunityMyPageListResponse> getAllPostsWithCommentsByUser(User writer, Pageable pageable) {
        return communityRepository.findAllByCommentWriterIdAndIsDeletedFalse(writer.getId(), IsDeleted.EXISTING, pageable)
                .map(CommunityMyPageListResponse::from);
    }


    @Override
    @Transactional
    public CommunityTypeResponse updatePost(Long communityId, CommunityWriteRequest postToUpdate) {
        try {
            Community existingCommunity = communityRepository.findById(communityId)
                    .orElseThrow(() -> new CustomException(CustomErrorCode.COMMUNITY_NOT_FOUND));

            // 제목, 글분류, 글내용, 모집상태 업데이트 (dirty checking)
            existingCommunity.update(postToUpdate);

            // 태그 업데이트
            if (postToUpdate.tags() != null) {
                communityWithTagService.saveTagToPost(existingCommunity, postToUpdate.tags());
            }

            // 리다이렉트할 때 넣어줄 글타입
            String typeForDto = setTypeForRedirect(existingCommunity);
            return new CommunityTypeResponse(typeForDto);

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
                    .orElseThrow(() -> new CustomException(CustomErrorCode.COMMUNITY_NOT_FOUND));

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
                    .orElseThrow(() -> new CustomException(CustomErrorCode.COMMUNITY_NOT_FOUND));

            if(!nowPost.getWriter().getProfile().getExternalId().equals(userNameFromJwtToken)) {
                throw new CustomException(CustomErrorCode.PERMISSION_ERROR);
            }

            // 북마크 연관관계 삭제: 게시글을 삭제해도 북마크는 남음
            nowPost.getBookmarks().forEach(CommunityBookmark::updateCommunityWhenDeleted);

            communityRepository.delete(nowPost);

        } catch (CustomException e) {
            throw e;

        } catch (Exception e) {
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
