package com.donguri.jejudorang.domain.trip.api;

import com.donguri.jejudorang.domain.trip.dto.response.TripApiResponseDto;
import com.donguri.jejudorang.domain.trip.entity.Trip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
public class TripApiController {
    @Value("${jeju-api-key}")
    private String apiKey;
    @Value("${jeju-api-uri}")
    private String baseUrl;

    private final String locale = "kr";
    private final List<String> categories = Arrays.asList("c1", "c2", "c4");

    @GetMapping("/trip/api/data")
    public String fetch() {
        // DefaultUriBuilderFactory implements UriBuilderFactory
        // URI에 alternative encoding mode를 설정해 UriBuilder 인스턴스를 생성할 수 있게 하는 DefaultUriBuilderFactory
        // WebClient를 이용하면 인코딩을 하지 않아 API KEY가 달라지는 문제 발생 가능
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY); // 인코딩 모드 설정; VALUES_ONLY: URI 템플릿은 인코딩하지않고, URI 변수를 템플릿으로 확장하기 전에 엄격한 인코딩 적용

        // WebClient 생성
        WebClient webClient = WebClient.builder() // WebClient.Builder 획득
                .uriBuilderFactory(factory) // 미리 설정해뒀던 UriBuilderFactory 인스턴스(factory) 삽입
                .baseUrl(baseUrl) // request baseURL 설정
                .build(); // WebClient 인스턴스 생성

        Mono<TripApiResponseDto> result = webClient.get() // HTTP GET Request 빌드 시작 - Returns: a spec for specifying the target URL (Interface WebClient.RequestHeadersUriSpec<S extends WebClient.RequestHeadersSpec<S>>)
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apiKey", apiKey)
                        .queryParam("locale", locale)
                        .queryParam("category", categories.toArray()) // Query Parameter 설정
                        .build()) // URI 빌드
                .retrieve() // Response를 추출할 방법 선언
                .bodyToMono(TripApiResponseDto.class);

        List<Trip> trips = result
                .map(TripApiResponseDto::toEntity)
                .block();

        

        log.info("trips:{}", trips);

        return "OK";
    }
}
