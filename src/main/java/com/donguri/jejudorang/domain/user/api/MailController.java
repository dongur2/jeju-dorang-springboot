package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.dto.request.email.*;
import com.donguri.jejudorang.domain.user.service.UserService;
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
public class MailController {

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
            checkValidationAndReturnException(bindingResult);

            userService.checkMailDuplicatedAndSendVerifyCode(mailSendRequest);

            log.info("이메일 인증 번호 전송 완료");
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (NullPointerException e) {
            log.error("이메일 인증 번호 전송 실패: {}", e.getMessage());
            return new ResponseEntity<>("이메일을 입력해주세요.", HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            log.error("이메일 인증 번호 전송 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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
            checkValidationAndReturnException(bindingResult);

            boolean checkRes = userService.checkVerifyMail(mailVerifyRequest);
            if (checkRes) {
                log.info("이메일 인증 완료 : 인증 번호 일치");
                return new ResponseEntity<>(HttpStatus.OK);

            } else {
                log.error("이메일 인증 실패 : 인증 번호 불일치");
                return new ResponseEntity<>("인증 번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST);
            }

        } catch (NullPointerException e) {
            log.error("이메일 인증 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_GATEWAY);
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
            checkValidationAndReturnException(bindingResult);

            userService.sendMailWithId(mailSendRequest);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("아이디 찾기 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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
            checkValidationAndReturnException(bindingResult);

            userService.checkUserAndSendVerifyCode(mailSendForPwdRequest);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("아이디 찾기 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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
            checkValidationAndReturnException(bindingResult);

            userService.changePwdRandomlyAndSendMail(mailSendForPwdRequest);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("비밀번호 재설정 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }







    /*
    * DTO Validation 에러 체크 후 에러 발생시에러 메세지 세팅한 Exception throw
    * */
    private static void checkValidationAndReturnException(BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            log.error("실패: {}", bindingResult.getFieldError().getDefaultMessage());
            throw new Exception(bindingResult.getFieldError().getDefaultMessage());
        }
    }



}
