package com.donguri.jejudorang.domain.trip.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Slf4j
@RestController
public class TripApiController {

    @Value("${jeju-api-key}") // application-API-KEY.properties
    private String apiKey;
    private final String baseUrl = "https://api.visitjeju.net/vsjApi/contents/searchList";
    private final String apiKeyParam = "?apiKey=";
    private final String defualtQueryParams = "&locale=kr&category=c1&category=c2&category=c4";

    private String setUrl() throws UnsupportedEncodingException {
        return baseUrl + apiKeyParam + apiKey + defualtQueryParams;
    }

    @GetMapping("/trip/api/data")
    public ResponseEntity<?> fetch() throws UnsupportedEncodingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<Map> resultMap = restTemplate.exchange(setUrl(), HttpMethod.GET, entity, Map.class);
        return resultMap;
    }
}
