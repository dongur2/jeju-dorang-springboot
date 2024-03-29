package com.donguri.jejudorang.global.auth.jwt;

import com.donguri.jejudorang.domain.user.entity.LoginType;
import com.donguri.jejudorang.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class JwtUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username; // externalId

    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public JwtUserDetails(Long id, String externalId, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = externalId;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static JwtUserDetails build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        if(user.getLoginType() == LoginType.BASIC) {
            return new JwtUserDetails(
                    user.getId(),
                    user.getProfile().getExternalId(),
                    user.getAuth().getEmail(),
                    user.getPwd().getPassword(),
                    authorities
            );

        } else {
            return new JwtUserDetails(
                    user.getId(),
                    user.getProfile().getExternalId(),
                    user.getAuth().getEmail(),
                    user.getAuth().getEmail(),
                    authorities
            );
        }

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username; // externalId
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JwtUserDetails user = (JwtUserDetails) o;
        return Objects.equals(id, user.id);
    }

}
