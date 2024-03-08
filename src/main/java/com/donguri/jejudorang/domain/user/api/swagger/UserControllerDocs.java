package com.donguri.jejudorang.domain.user.api.swagger;

import com.donguri.jejudorang.domain.user.dto.request.LoginRequest;
import com.donguri.jejudorang.domain.user.dto.request.SignUpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "User", description = "회원 가입/탈퇴/로그인 API")
public interface UserControllerDocs {

    @Operation(summary = "회원가입 폼 화면 출력")
    String registerForm();

    @Operation(summary = "회원가입", description = "사용자가 제공한 정보(SignUpRequest)를 기반으로 새로운 회원을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "회원가입 실패: 사용자 제공 정보(SignUpRequest)의 조건 미충족/비밀번호와 비밀번호 확인 불일치"),
            @ApiResponse(responseCode = "404", description = "회원가입 실패: 존재하지 않는 권한"),
            @ApiResponse(responseCode = "409", description = "회원가입 실패: 아이디 중복/이메일 중복"),
            @ApiResponse(responseCode = "500", description = "회원가입 실패: 서버 에러 발생")
    })
    ResponseEntity<?> registerUser(@Valid @ParameterObject SignUpRequest signUpRequest, BindingResult bindingResult);


    @Operation(summary = "로그인 폼 화면 출력")
    String signInForm(Model model);

    @Operation(summary = "로그인", description = "사용자가 제공한 정보(LoginRequest)를 기반으로 로그인을 수행하고, 성공할 경우 쿠키에 JWT 액세스 토큰과 리프레시 토큰을 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "로그인 실패: 사용자 제공 정보(LoginRequest)의 조건 미충족"),
            @ApiResponse(responseCode = "401", description = "로그인 실패: 틀린 비밀번호"),
            @ApiResponse(responseCode = "404", description = "로그인 실패: 가입된 아이디 없음"),
            @ApiResponse(responseCode = "500", description = "로그인 실패: 서버 에러 발생")
    })
    ResponseEntity<?> authenticateUser(@Valid @ParameterObject LoginRequest loginRequest, BindingResult bindingResult, HttpServletResponse response);

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Parameter(name = "type", description = "회원 타입: BASIC/KAKAO", example = "BASIC", required = true)
    @Operation(summary = "회원탈퇴", description = "현재 로그인한 사용자의 회원 정보를 데이터베이스에서 삭제합니다. 탈퇴한 사용자의 모든 데이터가 영구적으로 삭제됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원탈퇴 성공"),
            @ApiResponse(responseCode = "500", description = "회원탈퇴 실패: 서버 에러 발생")
    })
    ResponseEntity<?> deleteUser(@CookieValue("access_token") Cookie token, @RequestParam("type") String loginType);
}
