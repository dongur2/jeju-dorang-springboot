package com.donguri.jejudorang.domain.user.api.swagger;

import com.donguri.jejudorang.domain.user.dto.request.PasswordRequest;
import com.donguri.jejudorang.domain.user.dto.request.ProfileRequest;
import com.donguri.jejudorang.domain.user.dto.request.email.MailChangeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User: profile", description = "마이페이지 회원 프로필 관련 API")
public interface ProfileControllerDocs {

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "프로필 조회 후 화면 출력", description = "현재 로그인한 사용자의 프로필 정보를 조회하여 화면에 표시합니다. 로그인하지 않았거나 액세스 토큰이 유효하지 않을 경우 로그인 화면으로 리다이렉트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            @ApiResponse(responseCode = "404", description = "프로필 조회 실패: 가입된 회원 없음")
    })
    String getProfileForm(@CookieValue("access_token") Cookie token, Model model);

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "프로필 수정", description = "사용자가 제공한 정보(ProfileRequest)를 기반으로 프로필 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공"),
            @ApiResponse(responseCode = "400", description = "프로필 수정 실패: 사용자 제공 정보(ProfileRequest)의 조건 미충족"),
            @ApiResponse(responseCode = "404", description = "프로필 수정 실패: 가입된 회원 없음"),
            @ApiResponse(responseCode = "413", description = "프로필 수정 실패: 프로필 사진 파일 용량 초과"),
            @ApiResponse(responseCode = "415", description = "프로필 수정 실패: 프로필 사진 파일 형식 미지원"),
            @ApiResponse(responseCode = "500", description = "프로필 수정 실패: 서버 에러 발생")
    })
    ResponseEntity<?> updateProfile(@CookieValue("access_token") Cookie token, @Valid @ParameterObject ProfileRequest profileRequest, BindingResult bindingResult);

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "프로필 사진 삭제", description = "사용자가 설정한 개인 프로필 사진을 삭제하고 기본 프로필 사진으로 변경합니다. 이미 기본 프로필 사진인 경우 삭제되지 않습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 사진 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "프로필 사진 삭제 실패: 가입된 회원 없음"),
            @ApiResponse(responseCode = "500", description = "프로필 사진 삭제 실패: 서버 에러 발생")
    })
    ResponseEntity<HttpStatus> deleteProfileImg(@CookieValue("access_token") Cookie token);

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "이메일 변경", description = "현재 로그인한 사용자의 이메일을 새로운 이메일 주소로 변경합니다. 소셜 로그인 사용자의 경우에는 이 기능을 사용할 수 없습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 변경 성공"),
            @ApiResponse(responseCode = "400", description = "이메일 변경 실패: 사용자 제공 정보(MailChangeRequest)의 조건 미충족"),
            @ApiResponse(responseCode = "404", description = "이메일 변경 실패: 가입된 회원 없음"),
            @ApiResponse(responseCode = "500", description = "이메일 변경 실패: 서버 에러 발생")
    })
    ResponseEntity<?> updateEmail(@CookieValue("access_token") Cookie token, @RequestBody @Valid @ParameterObject MailChangeRequest mailChangeRequest,
                                  BindingResult bindingResult);


    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "비밀번호 변경 폼 화면 출력", description = "비밀번호 변경 폼 화면을 출력합니다.")
    String getUpdatePasswordForm(@CookieValue("access_token") Cookie token, Model model);

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 사용자의 비밀번호를 변경합니다. 소셜 로그인 사용자의 경우에는 이 기능을 사용할 수 없습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "비밀번호 변경 실패: 사용자 제공 정보(PasswordRequest)의 조건 미충족/새 비밀번호와 비밀번호 확인 불일치"),
            @ApiResponse(responseCode = "401", description = "비밀번호 변경 실패: 현재 비밀번호 불일치"),
            @ApiResponse(responseCode = "404", description = "비밀번호 변경 실패: 가입된 회원 없음"),
            @ApiResponse(responseCode = "500", description = "비밀번호 변경 실패: 서버 에러 발생")
    })
    ResponseEntity<?> updatePassword(@CookieValue("access_token") Cookie token, @Valid @ParameterObject PasswordRequest passwordRequest,
                                     BindingResult bindingResult);

}
