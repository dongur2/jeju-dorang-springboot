package com.donguri.jejudorang.domain.bookmark.service;

import com.donguri.jejudorang.domain.bookmark.entity.CommunityBookmark;
import com.donguri.jejudorang.domain.bookmark.entity.TripBookmark;
import com.donguri.jejudorang.domain.bookmark.repository.CommunityBookmarkRepository;
import com.donguri.jejudorang.domain.bookmark.repository.TripBookmarkRepository;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.trip.repository.TripRepository;
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

    @Override
    @Transactional
    public void addBookmark(String accessToken, String boardName, Long boardId) {

        try {
            User userForBookmark = getViewerFromJwt(accessToken);

            switch (boardName) {
                case "communities": addCommunityBookmark(boardId, userForBookmark); break;
                case "trips": addTripBookmark(boardId, userForBookmark); break;
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



    @Override
    @Transactional
    public void deleteBookmarkOnCommunity(String accessToken, Long communityId) {

        try {
            User userForBookmark = getViewerFromJwt(accessToken);

            Optional<CommunityBookmark> nullableBookmark = communityBookmarkRepository.findByUserAndCommunityId(userForBookmark, communityId);
            if(nullableBookmark.isEmpty()) {
                log.error("북마크한 글이 아닙니다.");
                throw new BadRequestException("북마크한 글이 아닙니다");
            }

            // Community bookmarks: orphanRemoval=true -> Community엔티티에서 북마크 삭제 -> 엔티티 자동 삭제
            communityRepository.findById(communityId)
                    .orElseThrow(() -> new EntityNotFoundException("해당하는 게시글이 없습니다"))
                            .updateBookmarks(nullableBookmark.get());
            log.info("북마크 삭제 완료");

        } catch (Exception e) {
            log.error("북마크 삭제 실패: {}", e.getMessage());
            throw (RuntimeException) e;
        }
    }


}
