package com.donguri.jejudorang.domain.user.api.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;

@Tag(name = "Admin", description = "관리자 관련 API")
public interface AdminControllerDocs {

    @Operation(summary = "관리자 회원가입 폼 화면 출력")
    String adminRegisterForm();

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "원격 저장소(S3)의 사용되지 않는 이미지 일괄 삭제", description = "게시글 작성중 이미지 업로드 후 작성 취소할 경우 버킷에 남는 이미지 같은 미사용 이미지들을 일괄 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "미사용 이미지 일괄 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "미사용 이미지 일괄 삭제 실패: 권한 없는 사용자"),
            @ApiResponse(responseCode = "500", description = "미사용 이미지 일괄 삭제 실패: 서버 에러 발생")
    })
    String deleteImages(@CookieValue("access_token") Cookie token, HttpServletResponse response, Model model);

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "마이페이지의 관리자페이지 화면을 출력")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 페이지 화면 출력 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 페이지 화면 출력 실패: 권한 없는 사용자"),
            @ApiResponse(responseCode = "500", description = "관리자 페이지 화면 출력 실패: 서버 에러 발생")
    })
    String adminPage(@CookieValue("access_token") Cookie token, HttpServletResponse response, Model model);

}
