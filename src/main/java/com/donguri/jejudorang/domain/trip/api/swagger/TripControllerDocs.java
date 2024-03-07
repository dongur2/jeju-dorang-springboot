package com.donguri.jejudorang.domain.trip.api.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Trip", description = "여행(trip) 관련 API")
public interface TripControllerDocs {

    @Parameter(name = "search", description = "태그 검색어", example = "구좌읍", required = false)
    @Parameter(name = "nowPage", description = "현재 페이지 번호", required = true)
    @Parameter(name = "category", description = "여행 데이터 정렬 기준", required = true, example = "전체/쇼핑/관광지/음식점")
    @Operation(summary = "여행지 글목록 조회", description = "DB로부터 태그 검색어, 정렬기준에 일치하는 여행지 데이터를 조회한 뒤, 조회한 데이터를 표시하는 Trip 게시판 메인 화면을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "여행지 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "여행지 목록 조회 실패")
    })
    String getTripList(@RequestParam(name = "search", required = false) String word,
                       @RequestParam(name = "nowPage", required = false, defaultValue = "0") Integer nowPage,
                       @RequestParam(name = "category", required = false, defaultValue = "전체") String category,
                       Model model, HttpServletResponse response);

    @Parameter(name = "placeId", description = "여행지 ID")
    @Parameter(name = "access_token", description = "JWT 액세스 토큰", required = false)
    @Operation(summary = "여행지 상세글 조회", description = "여행지 상세 데이터를 조회하여 표시한 여행지 ID에 해당하는 상세글 화면을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상세글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "실패: 존재하지 않는 여행지 ID")
    })
    String tripDetail(@PathVariable("placeId") Long placeId,
                      @CookieValue(required = false, name = "access_token") Cookie accessToken, Model model, HttpServletResponse response);

}
