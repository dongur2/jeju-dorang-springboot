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
    @Autowired private final JwtProvider jwtProvider;

    @Autowired private final UserRepository userRepository;
    @Autowired private final BookmarkRepository bookmarkRepository;
    @Autowired private final CommunityRepository communityRepository;

    public BookmarkServiceI(JwtProvider jwtProvider, UserRepository userRepository, BookmarkRepository bookmarkRepository, CommunityRepository communityRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.communityRepository = communityRepository;
    }

    @Override
    @Transactional
    public void addBookmarkOnCommunity(String accessToken, Long communityId) {

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

    @Override
    @Transactional
    public void deleteBookmarkOnCommunity(String accessToken, Long communityId) {

        try {
            String userNameFromJwtToken = jwtProvider.getUserNameFromJwtToken(accessToken);
            User userForBookmark = userRepository.findByExternalId(userNameFromJwtToken)
                    .orElseThrow(() -> new EntityNotFoundException("해당하는 유저가 없습니다."));

            Optional<Bookmark> nullableBookmark = bookmarkRepository.findByUserAndCommunityId(userForBookmark, communityId);
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
