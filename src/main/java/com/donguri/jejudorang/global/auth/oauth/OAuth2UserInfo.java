package com.donguri.jejudorang.global.auth.oauth;

import java.util.Map;

/*
* 소셜 로그인별로 데이터를 가져오는 방식이 다르므로
* 추상 클래스로 메서드만 정의
*
* */
public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getId(); // 소셜 식별 값 : 구글 - "sub", 카카오 - "id", 네이버 - "id"

    public abstract String getNickname();

    public abstract String getEmail();

}
