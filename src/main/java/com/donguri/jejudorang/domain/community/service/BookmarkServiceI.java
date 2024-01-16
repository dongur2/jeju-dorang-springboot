package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.entity.Bookmark;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.repository.BookmarkRepository;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BookmarkServiceI implements BookmarkService {
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void updateBoardLikedState(Long nowUserId, Long nowCommunityId) {
        if(bookmarkRepository.findByUserIdAndCommunityId(nowUserId, nowCommunityId).isPresent()) {
            bookmarkRepository.delete(bookmarkRepository.findByUserIdAndCommunityId(nowUserId, nowCommunityId).get());
            log.info("추천 삭제 완료");
        } else {
            User foundUser = userRepository.findById(nowUserId).get();
            Community foundBoard = communityRepository.findById(nowCommunityId).get();

            Bookmark LikedToUpdate = Bookmark.builder()
                    .user(foundUser)
                    .community(foundBoard)
                    .build();

            bookmarkRepository.save(LikedToUpdate);
            log.info("추천 추가 완료");
        }
    }
}
