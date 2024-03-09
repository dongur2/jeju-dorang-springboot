package com.donguri.jejudorang.domain.community.api.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Community: party", description = "동행(party) 모집상태 API")
public interface PartyControllerDocs {

    @Operation(summary = "동행 게시글의 모집상태 변경", description = "동행 게시글의 모집상태를 변경합니다. 이 기능은 작성자에게만 노출됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "동행 모집 상태 변경 성공"),
            @ApiResponse(responseCode = "404", description = "동행 모집 상태 변경 실패: 찾을 수 없는 게시글"),
            @ApiResponse(responseCode = "500", description = "동행 모집 상태 변경 실패: 서버 에러 발생")
    })
    ResponseEntity<?> modifyBoardJoinState(@PathVariable("communityId") Long communityId);

}
