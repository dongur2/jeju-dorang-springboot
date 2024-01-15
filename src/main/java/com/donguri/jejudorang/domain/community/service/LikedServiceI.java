package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.Liked;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.repository.LikedRepository;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LikedServiceI implements LikedService{
//    @Autowired
//    private LikedRepository likedRepository;
//    @Autowired
//    private CommunityRepository boardRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public void updateBoardLikedState(Long nowUserId, Long nowBoardId) {
//        if(likedRepository.findByUserIdAndBoardId(nowUserId, nowBoardId).isPresent()) {
//            likedRepository.delete(likedRepository.findByUserIdAndBoardId(nowUserId, nowBoardId).get());
//            log.info("추천 삭제 완료");
//        } else {
//            User foundUser = userRepository.findById(nowUserId).get();
//            Community foundBoard = boardRepository.findById(nowBoardId).get();
//
//            Liked LikedToUpdate = Liked.builder()
//                    .user(foundUser)
//                    .community(foundBoard)
//                    .build();
//
//            likedRepository.save(LikedToUpdate);
//            log.info("추천 추가 완료");
//        }
//    }
}
