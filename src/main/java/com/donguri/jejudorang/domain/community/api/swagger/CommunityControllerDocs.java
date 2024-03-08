package com.donguri.jejudorang.domain.community.api.swagger;

import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Community", description = "커뮤니티(community) 관련 API")
public interface CommunityControllerDocs {

    @Parameter(name = "type", description = "글 작성 화면에서 기본값으로 설정될 게시판 분류 타입", example = "party/chat",required = true)
    @Operation(summary = "글 작성 폼 화면 출력", description = "사용자가 바로 직전 머무르던 게시판의 타입에 따라 글 작성 폼 화면을 출력합니다. 로그인하지 않은 경우 로그인 폼 화면으로 리다이렉트됩니다.")
    String getCommunityWriteForm(@RequestParam(name = "type") String preType, Model model);

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "글 작성", description = "사용자가 제공한 정보(CommunityWriteRequest)를 기반으로 새로운 게시글을 게시판 분류에 맞게 데이터베이스에 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "글 작성 실패: 사용자 제공 정보(CommunityWriteRequest)의 조건 미충족/태그 이름 길이 초과"),
            @ApiResponse(responseCode = "404", description = "글 작성 실패: 찾을 수 없는 회원"),
            @ApiResponse(responseCode = "500", description = "글 작성 실패: 서버 에러 발생")
    })
    ResponseEntity<?> postNewCommunity(@Valid CommunityWriteRequest postToWrite, BindingResult bindingResult,
                                       @CookieValue("access_token") Cookie token);


    @Operation(summary = "글 수정 폼 출력", description = "사용자가 수정하려는 글 데이터를 조회하여 표시한 수정 폼 화면을 출력합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글 수정 폼 출력 성공"),
            @ApiResponse(responseCode = "404", description = "글 수정 폼 출력 실패: 찾을 수 없는 게시글"),
            @ApiResponse(responseCode = "500", description = "글 수정 폼 출력 실패: 서버 에러 발생"),
    })
    String getCommunityModifyForm(@PathVariable("communityId") Long communityId, Model model, HttpServletResponse response);

    @Operation(summary = "글 수정", description = "사용자가 제공한 정보(CommunityWriteRequest)를 기반으로 수정된 게시글을 데이터베이스에 저장합니다. 예외가 발생할 경우 예외 화면을 출력합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글 수정 성공: 수정된 상세글 페이지 uri를 포함한 ResponseEntity를 반환합니다."),
            @ApiResponse(responseCode = "400", description = "글 수정 실패: 사용자 제공 정보(CommunityWriteRequest)의 조건 미충족/태그 이름 길이 초과"),
            @ApiResponse(responseCode = "404", description = "글 수정 실패: 찾을 수 없는 회원"),
            @ApiResponse(responseCode = "500", description = "글 수정 실패: 서버 에러 발생")
    })
    ResponseEntity<?> modifyCommunity(@PathVariable("communityId") Long communityId, @Valid CommunityWriteRequest postToUpdate,
                                      BindingResult bindingResult);

    @Parameter(name = "type", example = "parties/chats")
    @Parameter(name = "page", description = "현재 페이지 번호")
    @Parameter(name = "state", description = "모임 게시판일 경우에만 해당", example = "all/recruiting/done")
    @Parameter(name = "order", description = "정렬 기준", example = "recent/comment/bookmark")
    @Parameter(name = "search", description = "검색어 (글제목)")
    @Parameter(name = "tags", description = "검색어 (태그 이름)", example = "태그1,태그2")
    @Operation(summary = "글 목록 조회 & 글 목록 화면 출력", description = "사용자가 선택한 게시판 분류, 정렬 기준, 검색어, 모집 상태와 일치하는 커뮤니티 글목록을 데이터베이스로부터 조회하여 표시한 글 목록 화면을 출력합니다. 예외가 발생할 경우 예외 화면을 출력합니다.")
    String getCommunityList(@PathVariable(name = "type") String type,
                            @RequestParam(name = "page", required = false, defaultValue = "0") Integer nowPage,
                            @RequestParam(name = "state", required = false, defaultValue = "all") String state, // all, recruiting, done
                            @RequestParam(name = "order", required = false, defaultValue = "recent") String order, // recent, comment, bookmark
                            @RequestParam(name = "search", required = false) String searchWord,
                            @RequestParam(name = "tags", required = false) String searchTag,
                            Model model);

    @Operation(summary = "상세글 조회 & 상세글 화면 출력", description = "상세글 화면을 출력합니다. 예외가 발생할 경우 예외 화면을 출력합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상세글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "상세글 조회 실패: 찾을 수 없는 게시글"),
            @ApiResponse(responseCode = "500", description = "상세글 조회 실패: 서버 에러 발생")
    })
    String getCommunityDetail(@PathVariable("communityId") Long communityId, HttpServletRequest request,
                              HttpServletResponse response, Model model);


    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "글 삭제", description = "게시글을 데이터베이스에서 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글 삭제 성공: 게시글 타입에 맞는 메인 글목록 화면의 uri를 포함한 ResponseEntity를 반환합니다."),
            @ApiResponse(responseCode = "400", description = "글 삭제 실패: 사용자 제공 정보(CommunityWriteRequest)의 조건 미충족/태그 이름 길이 초과"),
            @ApiResponse(responseCode = "403", description = "글 삭제 실패: 권한 없는 사용자 (작성자만 삭제 가능)"),
            @ApiResponse(responseCode = "404", description = "글 삭제 실패: 찾을 수 없는 게시글"),
            @ApiResponse(responseCode = "500", description = "글 삭제 실패: 서버 에러 발생")
    })
    ResponseEntity<?> deleteCommunity(@PathVariable("communityId") Long communityId, @PathVariable("type") String type,
                                      @CookieValue("access_token") Cookie accessToken);
}
