package com.donguri.jejudorang.domain.user.api;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getEmail();
    String getCode();
}
