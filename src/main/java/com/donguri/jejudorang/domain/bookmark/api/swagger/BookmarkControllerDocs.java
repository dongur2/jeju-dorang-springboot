package com.donguri.jejudorang.domain.bookmark.api.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Bookmark", description = "북마크 관련 API")
public interface BookmarkControllerDocs {

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "북마크 등록", description = "게시글에 대한 북마크를 등록합니다. 이 요청은 여행과 커뮤니티에서 공통으로 사용되며, 로그인한 사용자만 가능한 요청입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북마크 등록 성공"),
            @ApiResponse(responseCode = "403", description = "북마크 등록 실패: 사용자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "북마크 등록 실패: 존재하지 않는 회원/존재하지 않는 게시글"),
            @ApiResponse(responseCode = "409", description = "북마크 등록 실패: 이미 북마크한 게시글"),
    })
    ResponseEntity<String> createBookmark(@CookieValue("access_token") Cookie accessToken,
                                          @RequestParam("type") String type, @RequestParam("id") Long postId);


    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "북마크 삭제", description = "게시글에 대한 북마크를 삭제합니다. 이 요청은 여행과 커뮤니티에서 공통으로 사용되며, 로그인한 사용자만 가능한 요청입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북마크 삭제 성공"),
            @ApiResponse(responseCode = "204", description = "북마크 삭제: 북마크한 글이 아님"),
            @ApiResponse(responseCode = "403", description = "북마크 삭제 실패: 사용자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "북마크 삭제 실패: 존재하지 않는 회원/존재하지 않는 게시글"),
            @ApiResponse(responseCode = "409", description = "북마크 삭제 실패: 이미 북마크한 게시글"),
    })
    ResponseEntity<String> deleteBookmark(@CookieValue("access_token") Cookie accessToken,
                                          @RequestParam("type") String type, @RequestParam("id") Long postId);

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "이미 삭제된 글의 북마크 삭제", description = "이미 삭제된 게시글에 대한 북마크를 삭제합니다. 이 요청은 커뮤니티에서만 사용되며, 로그인한 사용자만 마이페이지 북마크 목록에서 가능한 요청입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북마크 삭제 성공"),
            @ApiResponse(responseCode = "204", description = "북마크 삭제: 북마크한 글이 아님"),
            @ApiResponse(responseCode = "500", description = "북마크 삭제 실패: 서버 에러 발생")
    })
    ResponseEntity<String> deleteCommunityBookmarkDirectly(@CookieValue("access_token") Cookie accessToken,
                                                           @RequestParam("id") Long bookmarkId);
}
