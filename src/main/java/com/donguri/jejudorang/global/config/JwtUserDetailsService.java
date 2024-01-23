package com.donguri.jejudorang.global.config;

import com.donguri.jejudorang.domain.user.entity.Role;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    public JwtUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
    * loadUserByUsername
    * 로그인 아이디로 레포지토리에서 찾아온 유저에 역할을 부여하고,
    * 유저 아이디, 비밀번호, 역할로 UerDetails 생성
    *
    * */
    @Override
    public UserDetails loadUserByUsername(String externalId) throws UsernameNotFoundException {
        User user = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("해당 아이디를 가진 유저가 없습니다: " + externalId));

        List<String> roles = new ArrayList<>();
        roles.add(Role.USER.toString());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getProfile().getExternalId())
                .password(user.getPassword().getPassword())
                .roles(roles.toArray(new String[0]))
                .build();
    }
}
