package com.donguri.jejudorang.global.config.jwt;

import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired private final UserRepository userRepository;

    public JwtUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional // loadUserByExternalId
    public UserDetails loadUserByUsername(String externalId) throws UsernameNotFoundException {
        User dbUser = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 아이디를 가진 유저가 없습니다: "+ externalId));
        log.info("아이디로 유저 찾기 완료(loadUserByUsername) : {} by {}", dbUser, externalId);
        return JwtUserDetails.build(dbUser);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User dbUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("해당 DB아이디를 가진 유저가 없습니다: "+ id));
        log.info("DB아이디로 유저 찾기 완료(loadUserById) : {} by {}", dbUser, id);
        return JwtUserDetails.build(dbUser);
    }


}
