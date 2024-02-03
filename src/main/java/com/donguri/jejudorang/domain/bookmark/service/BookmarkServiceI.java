package com.donguri.jejudorang.domain.bookmark.service;

import com.donguri.jejudorang.domain.bookmark.entity.Bookmark;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.bookmark.repository.BookmarkRepository;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.service.CommunityService;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import com.donguri.jejudorang.global.config.JwtProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class BookmarkServiceI implements BookmarkService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    @Transactional
    public void makeBookmarkOnCommunity(String accessToken, Long communityId) {

        try {
            String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(accessToken);
            User userForBookmark = userRepository.findByExternalId(userNameFromJwtToken)
                    .orElseThrow(() -> new EntityNotFoundException("해당하는 유저가 없습니다."));

            if (bookmarkRepository.findByUserAndCommunityId(userForBookmark, communityId).isPresent()) {
                log.error("이미 북마크한 글입니다.");
                throw new BadRequestException("이미 북마크한 글입니다");
            }

            Bookmark newBookmark = Bookmark.builder()
                    .user(userForBookmark)
                    .community(communityRepository.findById(communityId)
                            .orElseThrow(() -> new EntityNotFoundException("해당하는 게시글이 없습니다")))
                    .build();

            bookmarkRepository.save(newBookmark);

        } catch (BadRequestException e) {
            log.error("북마크 생성 실패 -- 중복: {}", e.getMessage());
            throw new RuntimeException("이미 북마크한 글입니다.");

        } catch (Exception e) {
            log.error("북마크 생성 실패: {}", e.getMessage());
            throw e;
        }

    }

//    @Override
//    @Transactional
//    public void changeCommunityBookmarkState(String token, Long nowCommunityId) {

//        String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(token);
//        User nowUser = userRepository.findByExternalId(userNameFromJwtToken)
//                .orElseThrow(() -> new EntityNotFoundException("해당 아이디를 가진 유저가 없습니다."));
//
//        Bookmark existingBookmark = bookmarkRepository.findByUserAndCommunityId(nowUser, nowCommunityId)
//                .orElseThrow(() -> new EntityNotFoundException("해당하는 북마크가 없습니다."));
//
//        log.info("북마크 삭제");
//        communityService.updateBookmarkState(existingBookmark);
//        bookmarkRepository.deleteById(existingBookmark.getId());
//
//
//        } else {
//            log.info("북마크 추가");
//            Community foundCommunity = communityRepository.findById(nowCommunityId)
//                    .orElseThrow(() -> new EntityNotFoundException("해당 글을 찾을 수 없습니다."));
//
//            Bookmark LikedToUpdate = Bookmark.builder()
//                    .user(nowUser)
//                    .community(foundCommunity)
//                    .build();
//
//            communityService.updateBookmarkState(LikedToUpdate);
//            bookmarkRepository.save(LikedToUpdate);
////        }
//
//    }
//
//    @Override
//    public void changeCommunityBookmarkState(User nowUser, Long nowBoardId) {
//
//    }
}
