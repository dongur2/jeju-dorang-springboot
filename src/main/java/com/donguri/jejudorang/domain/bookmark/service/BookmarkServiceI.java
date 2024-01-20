package com.donguri.jejudorang.domain.bookmark.service;

import com.donguri.jejudorang.domain.bookmark.entity.Bookmark;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.bookmark.repository.BookmarkRepository;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.service.CommunityService;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class BookmarkServiceI implements BookmarkService {
    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommunityService communityService;

    @Override
    @Transactional
    public void changeCommunityLikedState(User nowUser, Long nowCommunityId) {
        if(bookmarkRepository.findByUserAndCommunityId(nowUser, nowCommunityId).isPresent()) {
            bookmarkRepository.delete(bookmarkRepository.findByUserAndCommunityId(nowUser, nowCommunityId).get());
            log.info("추천 삭제 완료");
        } else {
            Community foundCommunity = communityRepository.findById(nowCommunityId).get();

            Bookmark LikedToUpdate = Bookmark.builder()
                    .user(nowUser)
                    .community(foundCommunity)
                    .build();

            Bookmark saved = bookmarkRepository.save(LikedToUpdate);
            communityService.updateBookmark(saved);

            log.info("bookmark count is {}",saved.getCommunity().getBookmarks().size());
            log.info("추천 추가 완료");
        }
    }
}
