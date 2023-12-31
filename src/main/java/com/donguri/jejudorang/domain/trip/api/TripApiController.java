package com.donguri.jejudorang.domain.trip.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
public class TripApiController {

    @Value("${jeju-api-key}") // application-API-KEY.properties
    private String apiKey;
    private final String baseUrl = "https://api.visitjeju.net/vsjApi/contents/searchList";
    private final String locale = "kr";
    private final List<String> categories = Arrays.asList("c1", "c2", "c4");

    @GetMapping("/trip/api/data")
    public String fetch() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                    .uriBuilderFactory(factory)
                    .baseUrl(baseUrl)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

        return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("apiKey", apiKey)
                            .queryParam("locale", locale)
                            .queryParam("category", categories.toArray())
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
    }
}
