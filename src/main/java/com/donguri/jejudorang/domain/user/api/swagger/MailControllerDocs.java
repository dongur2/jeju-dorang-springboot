package com.donguri.jejudorang.domain.user.api.swagger;

import com.donguri.jejudorang.domain.user.dto.request.email.MailSendForPwdRequest;
import com.donguri.jejudorang.domain.user.dto.request.email.MailSendRequest;
import com.donguri.jejudorang.domain.user.dto.request.email.MailVerifyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User: mail", description = "인증번호 메일 관련 API")
public interface MailControllerDocs {

    @Operation(summary = "인증번호 전송", description = "사용자가 제공한 정보(MailSendRequest)를 기반으로 이메일 중복을 확인하고, 중복이 아닐 경우에 입력받은 이메일로 인증번호를 포함한 메일을 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증번호 전송 성공"),
            @ApiResponse(responseCode = "400", description = "인증번호 전송 실패: 사용자 제공 정보(MailSendRequest)의 조건 미충족"),
            @ApiResponse(responseCode = "409", description = "인증번호 전송 실패: 이메일 중복"),
            @ApiResponse(responseCode = "500", description = "인증번호 전송 실패: 서버 에러 발생")
    })
    ResponseEntity<?> checkDuplicatedAndSendEmailCode(@RequestBody @Valid @ParameterObject MailSendRequest mailSendRequest, BindingResult bindingResult);

    @Operation(summary = "인증번호 확인", description = "사용자가 제공한 정보(MailVerifyRequest)를 기반으로 사용자가 입력한 인증번호가 서버에서 전송한 인증번호와 일치하는지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증번호 확인 성공"),
            @ApiResponse(responseCode = "400", description = "인증번호 확인 실패: 사용자 제공 정보(MailVerifyRequest)의 조건 미충족/인증번호 불일치/인증번호 만료"),
            @ApiResponse(responseCode = "500", description = "인증번호 확인 실패: 서버 에러 발생")
    })
    ResponseEntity<?> checkEmailCode(@RequestBody @Valid @ParameterObject MailVerifyRequest mailVerifyRequest, BindingResult bindingResult);

    @Operation(summary = "아이디 찾기 이메일 전송", description = "사용자가 제공한 정보(MailSendRequest)를 기반으로 사용자가 입력한 이메일 주소로 아이디를 포함한 이메일을 전송합니다. 이메일 주소와 일치하는 회원 정보가 없을 경우 404 예외를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 전송 성공"),
            @ApiResponse(responseCode = "400", description = "이메일 전송 실패: 사용자 제공 정보(MailSendRequest)의 조건 미충족"),
            @ApiResponse(responseCode = "404", description = "이메일 전송 실패: 입력 정보와 일치하는 회원 없음"),
            @ApiResponse(responseCode = "500", description = "이메일 전송 실패: 서버 에러 발생")
    })
    ResponseEntity<?> findId(@RequestBody @Valid @ParameterObject MailSendRequest mailSendRequest, BindingResult bindingResult);

    @Operation(summary = "비밀번호 찾기 이메일 전송", description = "사용자가 제공한 정보(MailSendForPwdRequest)를 기반으로 사용자가 입력한 이메일 주소로 인증번호를 전송합니다. 이메일 주소, 아이디와 일치하는 회원 정보가 없을 경우 404 예외를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 전송 성공"),
            @ApiResponse(responseCode = "400", description = "이메일 전송 실패: 사용자 제공 정보(MailSendForPwdRequest)의 조건 미충족"),
            @ApiResponse(responseCode = "404", description = "이메일 전송 실패: 입력 정보와 일치하는 회원 없음"),
            @ApiResponse(responseCode = "500", description = "이메일 전송 실패: 서버 에러 발생")
    })
    ResponseEntity<?> findPwd(@RequestBody @Valid @ParameterObject MailSendForPwdRequest mailSendForPwdRequest, BindingResult bindingResult);

    @Operation(summary = "임시 비밀번호 설정 & 이메일 전송", description = "사용자가 제공한 정보(MailSendForPwdRequest)를 기반으로, 서버에서 사용자의 비밀번호를 임의로 변경하고 임시 비밀번호를 사용자의 이메일 주소로 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 전송 성공"),
            @ApiResponse(responseCode = "400", description = "이메일 전송 실패: 사용자 제공 정보(MailSendForPwdRequest)의 조건 미충족"),
            @ApiResponse(responseCode = "404", description = "이메일 전송 실패: 입력 정보와 일치하는 회원 없음"),
            @ApiResponse(responseCode = "500", description = "이메일 전송 실패: 서버 에러 발생")
    })
    ResponseEntity<?> changePwd(@RequestBody @Valid @ParameterObject MailSendForPwdRequest mailSendForPwdRequest, BindingResult bindingResult);
}
