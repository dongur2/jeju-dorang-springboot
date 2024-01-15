package com.donguri.jejudorang.domain.community.service;

import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.repository.LikedRepository;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LikedServiceITest {

    @Autowired
    private LikedRepository likedRepository;
    @Autowired
    private CommunityRepository boardRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    LikedService likedService;

    @Test
    void 회원가입() {
        //given
        User userToJoin = new User("donga@mail.com", "둥가");
        //when
        User savedUser = userRepository.save(userToJoin);
        //then
        Assertions.assertThat(savedUser.getEmail()).isEqualTo(userToJoin.getEmail());
    }

    @Test
    void 추천() {
//        //given
//        Long userId = 1L;
//        Long boardId = 1L;
//        //when
//        likedService.updateBoardLikedState(userId, boardId);
//        //then
////        Assertions.assertThat(likedRepository.findByUserIdAndBoardId(userId, boardId).get().getBoard().getId()).isEqualTo(1L);
//        Assertions.assertThat(likedRepository.findByUserIdAndBoardId(userId, boardId)).isEmpty();
    }


}