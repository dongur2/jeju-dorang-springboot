package com.donguri.jejudorang.domain.trip.api.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Trip: 여행 데이터 API", description = "VISITJEJU Data API")
public interface TripApiControllerDocs {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "데이터 다운로드 성공",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
    })
    @Operation(summary = "여행지 공공데이터 다운로드", description = "API URI로부터 제주도 여행지 데이터를 받아와 카테고리별로 DB에 저장합니다.")
    public String fetch();

}
