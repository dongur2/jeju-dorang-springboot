package com.donguri.jejudorang.domain.trip.api;

import com.donguri.jejudorang.domain.trip.api.swagger.TripApiControllerDocs;
import com.donguri.jejudorang.domain.trip.dto.TripApiDataDto;
import com.donguri.jejudorang.domain.trip.service.TripService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
public class TripApiController implements TripApiControllerDocs {
    private final String apiKey;
    private final String baseUrl;

    private final String locale = "kr";
    private final List<String> categories = Arrays.asList("c1", "c2", "c4");

    private final TripService tripService;

    public TripApiController(@Value("${jeju.key}") String apiKey,
                             @Value("${jeju.url}") String baseUrl, TripService tripService) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.tripService = tripService;
    }

    @GetMapping("/trip/api/data")
    public String fetch() {

        long start = System.currentTimeMillis();

        // DefaultUriBuilderFactory implements UriBuilderFactory
        // URI에 alternative encoding mode를 설정해 UriBuilder 인스턴스를 생성할 수 있게 하는 DefaultUriBuilderFactory
        // WebClient를 이용하면 인코딩을 하지 않아 API KEY가 달라지는 문제 발생 가능
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY); // 인코딩 모드 설정; VALUES_ONLY: URI 템플릿은 인코딩하지않고, URI 변수를 템플릿으로 확장하기 전에 엄격한 인코딩 적용

        // WebClient 생성
        WebClient webClient = WebClient.builder() // WebClient.Builder 획득
                .uriBuilderFactory(factory) // 미리 설정해뒀던 UriBuilderFactory 인스턴스(factory) 삽입
                .baseUrl(baseUrl) // request baseURL 설정
                .codecs(clientCodecConfigurer -> clientCodecConfigurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build(); // WebClient 인스턴스 생성

        // 1페이지 먼저 요청해서 pageCount 받아오기
        TripApiDataDto firstPageDto = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apiKey", apiKey)
                        .queryParam("locale", locale)
                        .queryParam("category", categories.toArray())
                        .queryParam("page", 1)
                        .build())
                .retrieve()
                .bodyToMono(TripApiDataDto.class)
                .block(); // 동기적으로 첫 페이지 요청

        if (firstPageDto == null) {
            throw new RuntimeException("첫 페이지 API 호출 실패");
        }

        int maxPage = firstPageDto.pageCount(); // 전체 페이지 수 가져오기

        Flux.range(1, maxPage)
                .flatMap(pageNum -> webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("apiKey", apiKey)
                                .queryParam("locale", locale)
                                .queryParam("category", categories.toArray())
                                .queryParam("page", pageNum)
                                .build())
                        .retrieve()
                        .bodyToMono(TripApiDataDto.class))
                .doOnNext(tripService::saveApiTrips)
                .blockLast(); //마지막 요소 끝날 때까지 대기

        long finish = System.currentTimeMillis();

        System.out.println("응답 시간: " + (finish - start) + "ms"); //응답 시간: 3590ms 기존 29페이지 기준

        return "redirect:/trip/lists";
    }
}
