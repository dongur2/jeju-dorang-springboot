package com.donguri.jejudorang.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.function.Function;

@Configuration
public class SpringConfig {

    /*
    * 중복된 쿼리 파라미터 처리
    * 하나의 문자열 파라미터를 받아 하나의 문자열을 반환하는 함수
    * 현재 요청의 URL에서 지정된 쿼리 파라미터를 제거한 URL을 생성
    * 사용: th:with="currentUrl=(${@currentUrlWithoutParam.apply('order')})"
    *
    * */
    @Bean
    public Function<String, String> currentUrlWithoutParam() {
        return param -> ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam(param).toUriString();
    }
}
