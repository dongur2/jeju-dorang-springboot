package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.api.swagger.AdminControllerDocs;
import com.donguri.jejudorang.domain.user.service.AdminService;
import com.donguri.jejudorang.global.error.CustomException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController implements AdminControllerDocs {

    @Autowired private final AdminService adminService;
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /*
    * 관리자 회원가입 폼 화면 출력
    * */
    @GetMapping
    public String adminRegisterForm() {
        return "user/admin/signUpForm";
    }


    /*
    * S3 Bucket의 사용되지 않는 이미지 삭제
    * - 게시글 작성중 이미지 업로드 후 글 작성 미완료/취소할 경우 버킷에 남는 이미지
    * */
    @GetMapping("/img")
    public String deleteImages(@CookieValue("access_token") Cookie token, HttpServletResponse response, Model model) {
       try {
           adminService.deleteUnusedImages(token.getValue());
           return "redirect:/";

       } catch (Exception e) {
           log.error("이미지 삭제 실패: {}", e.getMessage());
           response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
           model.addAttribute("message", e.getMessage());
           return "error/500";
       }
    }

    @GetMapping("/features")
    public String adminPage(@CookieValue("access_token") Cookie token, HttpServletResponse response, Model model) {
        try {
            adminService.checkIsAdmin(token.getValue());
            return "user/admin/adminFeatures";

        } catch (CustomException e) {
            log.error("관리자 페이지 조회 실패: {}",e.getCustomErrorCode().getMessage());
            response.setStatus(e.getCustomErrorCode().getStatus().value());
            return "error/403";

        } catch (Exception e) {
            log.error("관리자 페이지 조회 실패; 서버 오류 {}", e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }
}
