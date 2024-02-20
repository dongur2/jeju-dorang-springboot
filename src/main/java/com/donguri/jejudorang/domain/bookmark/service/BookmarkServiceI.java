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
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class BookmarkServiceI implements BookmarkService {
    @Autowired private final JwtProvider jwtProvider;

    @Autowired private final UserRepository userRepository;
    @Autowired private final TripRepository tripRepository;
    @Autowired private final CommunityRepository communityRepository;
    @Autowired private final TripBookmarkRepository tripBookmarkRepository;
    @Autowired private final CommunityBookmarkRepository communityBookmarkRepository;

    public BookmarkServiceI(JwtProvider jwtProvider, UserRepository userRepository, TripRepository tripRepository, CommunityBookmarkRepository communityBookmarkRepository, TripBookmarkRepository tripBookmarkRepository, CommunityRepository communityRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.communityRepository = communityRepository;
        this.tripBookmarkRepository = tripBookmarkRepository;
        this.communityBookmarkRepository = communityBookmarkRepository;
    }

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

        } catch (BadRequestException e) {
            log.error("북마크 생성 실패 -- 중복: {}", e.getMessage());
            throw new RuntimeException("이미 북마크한 글입니다.");

        } catch (Exception e) {
            log.error("북마크 생성 실패: {}", e.getMessage());
            throw e;
        }

    }

    private User getViewerFromJwt(String accessToken) {
        String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(accessToken);
        return userRepository.findByExternalId(userNameFromJwtToken)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 유저가 없습니다."));
    }

    // Trip
    private void addTripBookmark(Long boardId, User userForBookmark) throws BadRequestException {
        if (tripBookmarkRepository.findByUserAndTripId(userForBookmark, boardId).isPresent()) {
            log.error("이미 북마크한 글입니다.");
            throw new BadRequestException("이미 북마크한 글입니다");
        }

        TripBookmark newBookmark = TripBookmark.builder()
                .user(userForBookmark)
                .trip(tripRepository.findById(boardId)
                        .orElseThrow(() -> new EntityNotFoundException("해당하는 게시글이 없습니다")))
                .build();

        tripBookmarkRepository.save(newBookmark);
    }

    // Community
    private void addCommunityBookmark(Long boardId, User userForBookmark) throws BadRequestException {
        if (communityBookmarkRepository.findByUserAndCommunityId(userForBookmark, boardId).isPresent()) {
            log.error("이미 북마크한 글입니다.");
            throw new BadRequestException("이미 북마크한 글입니다");
        }

        CommunityBookmark newBookmark = CommunityBookmark.builder()
                .user(userForBookmark)
                .community(communityRepository.findById(boardId)
                        .orElseThrow(() -> new EntityNotFoundException("해당하는 게시글이 없습니다")))
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

        } catch (Exception e) {
            log.error("북마크 삭제 실패: {}", e.getMessage());
            throw (RuntimeException) e;
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
            log.error("북마크 삭제 실패: {}", e.getMessage());
            throw (RuntimeException) e;
        }
    }


    // * 커뮤니티 북마크 삭제
    private void deleteCommunityBookmark(Long postId, User userForBookmark) throws BadRequestException {
        Optional<CommunityBookmark> bookmark = communityBookmarkRepository.findByUserAndCommunityId(userForBookmark, postId);

        // 북마크한 글이 아닐 경우 예외 리턴
        if(bookmark.isEmpty()) {
            throw new BadRequestException("북마크한 글이 아닙니다.");
        }

        Optional<Community> community = Optional.ofNullable(bookmark.get().getCommunity());

        // 북마크한 글이 삭제됐을 경우: 레포지토리에서만 삭제 / 글이 존재할 경우: 레포지토리 & 커뮤니티에서 삭제
        communityBookmarkRepository.delete(bookmark.get());
        community.ifPresent(post -> post.deleteBookmark(bookmark.get()));
    }

    // * 이미 삭제된 커뮤니티글의 북마크 삭제
    private void deleteCommunityBookmarkAlreadyDeleted(Long bookmarkId) throws BadRequestException {
        Optional<CommunityBookmark> bookmark = communityBookmarkRepository.findById(bookmarkId);

        if(bookmark.isEmpty()) {
            throw new BadRequestException("북마크한 글이 아닙니다.");
        }

        communityBookmarkRepository.delete(bookmark.get());
    }

    // * 여행 북마크 삭제
    private void deleteTripBookmark(Long postId, User userForBookmark) throws BadRequestException {
        Optional<TripBookmark> bookmark = tripBookmarkRepository.findByUserAndTripId(userForBookmark, postId);

        if(bookmark.isEmpty()) {
            log.error("북마크한 글이 아닙니다.");
            throw new BadRequestException("북마크한 글이 아닙니다");
        }

        // Trip bookmarks: orphanRemoval=true -> Trip엔티티에서 북마크 삭제 -> 엔티티 자동 삭제
        tripRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 게시글이 없습니다"))
                .updateBookmarks(bookmark.get());
        log.info("북마크 삭제 완료");
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
