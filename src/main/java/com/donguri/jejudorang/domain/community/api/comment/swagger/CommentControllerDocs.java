package com.donguri.jejudorang.domain.community.api.comment.swagger;

import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequest;
import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequestWithId;
import com.donguri.jejudorang.domain.community.dto.request.comment.RecommentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Community: comment", description = "댓글 관련 API")
public interface CommentControllerDocs {

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "댓글 작성", description = "사용자가 제공한 정보(CommentRequest)를 기반으로 댓글을 데이터베이스에 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "댓글 작성 실패: CommentRequest 조건 미충족"),
            @ApiResponse(responseCode = "500", description = "댓글 작성 실패: 서버 에러 발생")
    })
    ResponseEntity<?> createNewComment(@CookieValue("access_token") Cookie token,
                                       @Valid CommentRequest commentRequest, BindingResult bindingResult,
                                       @RequestParam("type") String type);

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "대댓글 작성", description = "사용자가 제공한 정보(ReCommentRequest)를 기반으로 대댓글을 데이터베이스에 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대댓글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "대댓글 작성 실패: ReCommentRequest 조건 미충족"),
            @ApiResponse(responseCode = "500", description = "대댓글 작성 실패: 서버 에러 발생")
    })
    ResponseEntity<?> createNewReComment(@CookieValue("access_token") Cookie token,
                                         @Valid RecommentRequest request, BindingResult bindingResult,
                                         @RequestParam("type") String type);


    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "댓글 수정", description = "사용자가 제공한 정보(CommentRequestWithId)를 기반으로 수정한 댓글을 데이터베이스에 저장합니다. 이 요청은 댓글과 대댓글 공통으로 사용됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "댓글 수정 실패: CommentRequestWithId 조건 미충족"),
            @ApiResponse(responseCode = "403", description = "댓글 수정 실패: 권한 없는 사용자 (작성자/관리자만 삭제 가능)"),
            @ApiResponse(responseCode = "404", description = "댓글 수정 실패: 존재하지 않는 댓글"),
            @ApiResponse(responseCode = "500", description = "댓글 수정 실패: 서버 에러 발생")
    })
    ResponseEntity<?> updateComment(@CookieValue("access_token") Cookie token, @RequestBody @Valid CommentRequestWithId commentRequest, BindingResult bindingResult);


    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제 처리합니다. 댓글의 데이터베이스 영구 삭제는 해당 게시글이 삭제될 때 수행됩니다. 이 요청은 댓글과 대댓글 공통으로 사용됩니다. 로그인한 유저가 작성자나 관리자가 아닐 경우 403 예외를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "댓글 삭제 실패: 권한 없는 사용자 (작성자/관리자만 삭제 가능)"),
            @ApiResponse(responseCode = "404", description = "댓글 삭제 실패: 존재하지 않는 댓글"),
            @ApiResponse(responseCode = "500", description = "댓글 삭제 실패: 서버 에러 발생")
    })
    ResponseEntity<?> deleteComment(@CookieValue("access_token") Cookie token, @RequestParam("cmtId") Long cmtId);

}
