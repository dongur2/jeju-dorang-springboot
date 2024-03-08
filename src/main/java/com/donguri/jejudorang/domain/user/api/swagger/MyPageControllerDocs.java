package com.donguri.jejudorang.domain.user.api.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "User: mypage", description = "마이페이지 작성글/북마크 관련 API")
public interface MyPageControllerDocs {

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Parameter(name = "page", description = "현재 페이지 번호", required = false)
    @Operation(summary = "작성글 조회 후 화면 출력", description = "현재 로그인한 사용자의 작성글을 조회하여 화면에 표시합니다. 예외가 발생하면 에러 페이지를 출력합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작성글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "작성글 조회 실패: 가입된 회원 없음"),
            @ApiResponse(responseCode = "500", description = "작성글 조회 실패: 서버 에러 발생")
    })
    String getMyWritingsPage(@CookieValue("access_token") Cookie token, Model model,
                             @RequestParam(name = "page", required = false, defaultValue = "0") Integer nowPage);

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Parameter(name = "page", description = "현재 페이지 번호", required = false)
    @Operation(summary = "댓글 작성한 글 조회 후 화면 출력", description = "현재 로그인한 사용자가 댓글을 작성한 글을 조회하여 화면에 표시합니다. 예외가 발생하면 에러 페이지를 출력합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 작성한 글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "댓글 작성한 글 조회 실패: 가입된 회원 없음"),
            @ApiResponse(responseCode = "500", description = "댓글 작성한 글 조회 실패: 서버 에러 발생")
    })
    String getMyCommentsPage(@CookieValue("access_token") Cookie token, Model model,
                             @RequestParam(name = "page", required = false, defaultValue = "0") Integer nowPage);

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Parameter(name = "type", description = "게시판 분류", example = "trip/community", required = false)
    @Parameter(name = "page", description = "현재 페이지 번호", required = false)
    @Operation(summary = "북마크한 글 조회 후 화면 출력", description = "현재 로그인한 사용자가 북마크한 글을 조회하여 여행 게시판과 커뮤니티 게시판을 구분하여 화면에 표시합니다. 예외가 발생하면 에러 페이지를 출력합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북마크한 글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "북마크한 글 조회 실패: 가입된 회원 없음"),
            @ApiResponse(responseCode = "500", description = "북마크한 글 조회 실패: 서버 에러 발생")
    })
    String getMyBookmarkPage(@CookieValue("access_token") Cookie token, Model model,
                             @RequestParam(name = "type", required = false, defaultValue = "trip") String type,
                             @RequestParam(name = "page", required = false, defaultValue = "0") Integer nowPage);
    
}
