package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.api.swagger.MailControllerDocs;
import com.donguri.jejudorang.domain.user.dto.request.email.*;
import com.donguri.jejudorang.domain.user.service.UserService;
import com.donguri.jejudorang.global.error.CustomErrorCode;
import com.donguri.jejudorang.global.error.CustomException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequestMapping("/email")
public class MailController implements MailControllerDocs {

    @Autowired private final UserService userService;

    public MailController(UserService userService) {
        this.userService = userService;
    }


    /*
    * 이메일 중복 확인 후 인증 번호 전송
    * POST
    *
    * */
    @PostMapping("/available")
    public ResponseEntity<?> checkDuplicatedAndSendEmailCode(@RequestBody @Valid MailSendRequest mailSendRequest, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }

            userService.checkMailDuplicatedAndSendVerifyCode(mailSendRequest);

            log.info("이메일 인증 번호 전송 완료");
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (CustomException e) {
            log.error("CUSTOM 이메일 인증 번호 전송 실패: {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (NullPointerException e) {
            log.error("이메일 인증 번호 전송 실패: {}", e.getMessage());
            return new ResponseEntity<>("이메일을 입력해주세요.", HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            log.error("이메일 인증 번호 전송 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /*
    * 이메일 인증 번호 확인
    * POST
    *
    * */
    @PostMapping("/verify")
    public ResponseEntity<?> checkEmailCode(@RequestBody @Valid MailVerifyRequest mailVerifyRequest, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }

            boolean checkRes = userService.checkVerifyMail(mailVerifyRequest);
            if (checkRes) {
                log.info("이메일 인증 완료 : 인증 번호 일치");
                return new ResponseEntity<>(HttpStatus.OK);

            } else {
                log.error("이메일 인증 실패 : 인증 번호 불일치");
                return new ResponseEntity<>("인증 번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST);
            }

        } catch (CustomException e) {
            log.error("이메일 인증 실패: {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("이메일 인증 실패: [서버 오류] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /*
    * 아이디 찾기 이메일 전송
    * POST
    *
    * */
    @PostMapping("/help/id")
    public ResponseEntity<?> findId(@RequestBody @Valid MailSendRequest mailSendRequest, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }

            userService.sendMailWithId(mailSendRequest);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (CustomException e) {
            log.error("아이디 찾기 실패: {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("아이디 찾기 실패: [서버 오류] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /*
    * 비밀번호 찾기 이메일 전송
    * POST
    *
    * */
    @PostMapping("/help/forPwd")
    public ResponseEntity<?> findPwd(@RequestBody @Valid MailSendForPwdRequest mailSendForPwdRequest, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }

            userService.checkUserAndSendVerifyCode(mailSendForPwdRequest);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (CustomException e) {
            log.error("아이디 찾기 실패: {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("아이디 찾기 실패: [서버 오류] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /*
    * 랜덤 비밀번호 설정: 이메일 전송
    * POST
    *
    * */
    @PostMapping("/help/pwd")
    public ResponseEntity<?> changePwd(@RequestBody @Valid MailSendForPwdRequest mailSendForPwdRequest, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }

            userService.changePwdRandomlyAndSendMail(mailSendForPwdRequest);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (CustomException e) {
            log.error("비밀번호 재설정 실패: {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("비밀번호 재설정 실패: [서버 오류] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
