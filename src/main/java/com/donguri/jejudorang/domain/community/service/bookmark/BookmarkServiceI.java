package com.donguri.jejudorang.domain.community.service.bookmark;

import com.donguri.jejudorang.domain.community.entity.bookmark.Bookmark;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.repository.bookmark.BookmarkRepository;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
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

    @Override
    @Transactional
    public void changeCommunityLikedState(Long nowUserId, Long nowCommunityId) {
        if(bookmarkRepository.findByUserIdAndCommunityId(nowUserId, nowCommunityId).isPresent()) {
            bookmarkRepository.delete(bookmarkRepository.findByUserIdAndCommunityId(nowUserId, nowCommunityId).get());
            log.info("추천 삭제 완료");
        } else {
            User foundUser = userRepository.findById(nowUserId).get();
            Community foundCommunity = communityRepository.findById(nowCommunityId).get();

            Bookmark LikedToUpdate = Bookmark.builder()
                    .user(foundUser)
                    .community(foundCommunity)
                    .build();

            bookmarkRepository.save(LikedToUpdate);
            log.info("추천 추가 완료");
        }
    }
}
