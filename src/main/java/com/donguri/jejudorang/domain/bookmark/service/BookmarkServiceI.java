package com.donguri.jejudorang.domain.bookmark.service;

import com.donguri.jejudorang.domain.bookmark.entity.CommunityBookmark;
import com.donguri.jejudorang.domain.bookmark.entity.TripBookmark;
import com.donguri.jejudorang.domain.bookmark.repository.CommunityBookmarkRepository;
import com.donguri.jejudorang.domain.bookmark.repository.TripBookmarkRepository;
import com.donguri.jejudorang.domain.community.dto.response.CommunityMyPageListResponse;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.trip.dto.response.TripListResponseDto;
import com.donguri.jejudorang.domain.trip.repository.TripRepository;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import com.donguri.jejudorang.global.auth.jwt.JwtProvider;
import com.donguri.jejudorang.global.error.CustomErrorCode;
import com.donguri.jejudorang.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkServiceI implements BookmarkService {
    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final CommunityRepository communityRepository;
    private final TripBookmarkRepository tripBookmarkRepository;
    private final CommunityBookmarkRepository communityBookmarkRepository;

    /*
    * 북마크 생성
    * */
    @Override
    @Transactional
    public void addBookmark(String accessToken, String boardName, Long boardId) {
        try {
            // 현재 로그인한 유저
            User userForBookmark = getViewerFromJwt(accessToken);

            // 요청 매핑의 게시판 이름에 따른 메서드 호출
            switch (boardName) {
                case "community": addCommunityBookmark(boardId, userForBookmark); break;
                case "trip": addTripBookmark(boardId, userForBookmark); break;
            }

        } catch (CustomException e) {
            log.error("북마크 생성 실패: {}", e.getCustomErrorCode().getMessage());
            throw e;
        }
    }

    private User getViewerFromJwt(String accessToken) {
        String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(accessToken);
        return userRepository.findByExternalId(userNameFromJwtToken)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    // Trip
    private void addTripBookmark(Long boardId, User userForBookmark) {
        if (tripBookmarkRepository.findByUserAndTripId(userForBookmark, boardId).isPresent()) {
            log.error("이미 북마크한 글입니다.");
            throw new CustomException(CustomErrorCode.ALREADY_BOOKMARKED);
        }

        TripBookmark newBookmark = TripBookmark.builder()
                .user(userForBookmark)
                .trip(tripRepository.findById(boardId)
                        .orElseThrow(() -> new CustomException(CustomErrorCode.TRIP_NOT_FOUND)))
                .build();

        tripBookmarkRepository.save(newBookmark);
    }

    // Community
    private void addCommunityBookmark(Long boardId, User userForBookmark) {
        if (communityBookmarkRepository.findByUserAndCommunityId(userForBookmark, boardId).isPresent()) {
            log.error("이미 북마크한 글입니다.");
            throw new CustomException(CustomErrorCode.ALREADY_BOOKMARKED);
        }

        CommunityBookmark newBookmark = CommunityBookmark.builder()
                .user(userForBookmark)
                .community(communityRepository.findById(boardId)
                        .orElseThrow(() -> new CustomException(CustomErrorCode.COMMUNITY_NOT_FOUND)))
                .build();

        communityBookmarkRepository.save(newBookmark);
    }



    /*
    * 북마크 삭제
    *
    * */
    @Override
    @Transactional
    public void deleteBookmark(String accessToken, String postType, Long postId) {

        try {
            User userForBookmark = getViewerFromJwt(accessToken);

            if(postType.equals("community")) {
                deleteCommunityBookmark(postId, userForBookmark);
            } else {
                deleteTripBookmark(postId, userForBookmark);
            }

        } catch (CustomException e) {
            log.error("북마크 삭제 실패: {}", e.getCustomErrorCode().getMessage());
            throw e;
        }
    }

    /*
    * 삭제된 글에 대한 북마크 삭제: 커뮤니티
    *
    * */
    @Override
    @Transactional
    public void deleteCommunityBookmarkOnDeletedPost(String accessToken, Long bookmarkId) {
        try {
            jwtProvider.validateJwtToken(accessToken);

            deleteCommunityBookmarkAlreadyDeleted(bookmarkId);

        } catch (Exception e) {
            throw (RuntimeException) e;
        }
    }


    // * 커뮤니티 북마크 삭제
    private void deleteCommunityBookmark(Long postId, User userForBookmark) {
        Optional<CommunityBookmark> bookmark = communityBookmarkRepository.findByUserAndCommunityId(userForBookmark, postId);

        // 북마크한 글이 아닐 경우 예외 리턴
        if(bookmark.isEmpty()) {
            throw new CustomException(CustomErrorCode.NOT_BOOKMARKED);
        }

        Optional<Community> community = Optional.ofNullable(bookmark.get().getCommunity());

        // 북마크한 글이 삭제됐을 경우: 레포지토리에서만 삭제 / 글이 존재할 경우: 레포지토리 & 커뮤니티에서 삭제
        communityBookmarkRepository.delete(bookmark.get());
        community.ifPresent(post -> post.deleteBookmark(bookmark.get()));
    }

    // * 이미 삭제된 커뮤니티글의 북마크 삭제
    private void deleteCommunityBookmarkAlreadyDeleted(Long bookmarkId) {
        Optional<CommunityBookmark> bookmark = communityBookmarkRepository.findById(bookmarkId);

        if(bookmark.isEmpty()) {
            throw new CustomException(CustomErrorCode.NOT_BOOKMARKED);
        }

        communityBookmarkRepository.delete(bookmark.get());
    }

    // * 여행 북마크 삭제
    private void deleteTripBookmark(Long postId, User userForBookmark) {
        Optional<TripBookmark> bookmark = tripBookmarkRepository.findByUserAndTripId(userForBookmark, postId);

        if(bookmark.isEmpty()) {
            throw new CustomException(CustomErrorCode.NOT_BOOKMARKED);
        }

        // Trip bookmarks: orphanRemoval=true -> Trip엔티티에서 북마크 삭제 -> 엔티티 자동 삭제
        tripRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.TRIP_NOT_FOUND))
                .updateBookmarks(bookmark.get());
    }


    /*
    * 북마크 조회
    * */
    @Override
    @Transactional
    public Page<?> getMyBookmarks(User user, String type, Pageable pageable) {
        try {
            if (type.equals("trip")) {
                return tripBookmarkRepository.findAllByUser(user, pageable)
                            .map(bookmark -> new TripListResponseDto(bookmark.getTrip()));
            } else {
                return communityBookmarkRepository.findAllByUser(user, pageable)
                        .map(bookmark -> CommunityMyPageListResponse.from(bookmark.getId(), bookmark.getCommunity()));
            }

        } catch (Exception e) {
            log.error("북마크 불러오기에 실패했습니다. {}", e.getMessage());
            throw e;
        }
    }

    /*
    * 회원의 모든 북마크 삭제
    *
    * */
    @Override
    @Transactional
    public void deleteAllBookmarksOfUser(Long userId) {
        try {

            /*
            * 북마크한 글이 존재할 경우 북마크한 글의 북마크 삭제(전이), 없을 경우 북마크를 삭제
            * */
            tripBookmarkRepository.findAllByUserId(userId)
                    .forEach(tb -> tripRepository.findById(tb.getTrip().getId())
                            .ifPresentOrElse(
                                    trip -> trip.deleteBookmark(tb),
                                    () -> tripBookmarkRepository.delete(tb)));


             communityBookmarkRepository.findAllByUserId(userId)
                    .forEach(cb -> communityRepository.findById(cb.getCommunity().getId())
                            .ifPresentOrElse(
                                    community -> {
                                        community.deleteBookmark(cb);
                                        communityBookmarkRepository.delete(cb);
                                    },
                                    () -> communityBookmarkRepository.delete(cb)));

        } catch (Exception e) {
            log.error("북마크 삭제에 실패했습니다. {}",  e.getMessage());
            throw e;
        }
    }
}
