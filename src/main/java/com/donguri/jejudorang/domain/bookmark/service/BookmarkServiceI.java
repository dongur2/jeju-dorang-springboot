package com.donguri.jejudorang.domain.bookmark.service;

import com.donguri.jejudorang.domain.bookmark.entity.Bookmark;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.bookmark.repository.BookmarkRepository;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.service.CommunityService;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    @Transactional
    public void changeCommunityBookmarkState(User nowUser, Long nowCommunityId) {
        Optional<Bookmark> existing = bookmarkRepository.findByUserAndCommunityId(nowUser, nowCommunityId);

        if (existing.isPresent()) { // 이미 북마크한 글일 경우
            log.info("북마크 삭제");
            communityService.updateBookmarkState(existing.get());
            bookmarkRepository.deleteById(existing.get().getId());
        } else {
            log.info("북마크 추가");
            Community foundCommunity = communityRepository.findById(nowCommunityId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 글을 찾을 수 없습니다."));

            Bookmark LikedToUpdate = Bookmark.builder()
                    .user(nowUser)
                    .community(foundCommunity)
                    .build();

            communityService.updateBookmarkState(LikedToUpdate);
            bookmarkRepository.save(LikedToUpdate);
        }

    }
}