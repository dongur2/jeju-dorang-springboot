package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.community.dto.response.CommunityBookmarkListResponse;
import com.donguri.jejudorang.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/mypage")
public class MyPageController {

    @Autowired private final UserService userService;

    public MyPageController(UserService userService) {
        this.userService = userService;
    }


    /*
     * 마이페이지 - 작성글 목록: 커뮤니티
     * GET
     *
     * */
    @GetMapping("/writings")
    public String getMyWritingsPage(@CookieValue("access_token") Cookie token, Model model,
                                    @RequestParam(name = "page", required = false, defaultValue = "0") Integer nowPage) {
        try {
            Pageable pageable = PageRequest.of(nowPage, 10, Sort.by("createdAt").descending());

            Page<CommunityBookmarkListResponse> data = userService.getMyCommunityWritings(token.getValue(), pageable);

            model.addAttribute("nowPage", nowPage);
            model.addAttribute("endPage", data.getTotalPages());
            model.addAttribute("posts", data);

            return "/user/mypage/myWritings";

        } catch (Exception e) {
            log.error("커뮤니티 작성글 불러오기 실패: {}", e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "/error/errorPage";
        }
    }


    /*
    * 마이페이지 - 댓글 단 글 목록: 커뮤니티
    * GET
    *
    * */
    @GetMapping("/comments")
    public String getMyCommentsPage(@CookieValue("access_token") Cookie token, Model model,
                                    @RequestParam(name = "page", required = false, defaultValue = "0") Integer nowPage) {
        try {
            Pageable pageable = PageRequest.of(nowPage, 10, Sort.by("createdAt").descending());

            Page<CommunityBookmarkListResponse> data = userService.getMyCommunityComments(token.getValue(), pageable);

            model.addAttribute("nowPage", nowPage);
            model.addAttribute("endPage", data.getTotalPages());
            model.addAttribute("posts", data);

            return "/user/mypage/myWritings";

        } catch (Exception e) {
            log.error("커뮤니티 작성댓글 불러오기 실패: {}", e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "/error/errorPage";
        }
    }


    /*
     * 마이페이지 - 북마크 목록: 여행/커뮤니티
     * GET
     *
     * */
    @GetMapping("/bookmarks")
    public String getMyBookmarkPage (@CookieValue("access_token") Cookie token, Model model,
                                        @RequestParam(name = "type", required = false, defaultValue = "trip") String type,
                                        @RequestParam(name = "page", required = false, defaultValue = "0") Integer nowPage){
        try {
            Pageable pageable = PageRequest.of(nowPage, 10, Sort.by("createdAt").descending());
            Page<?> data = userService.getMyBookmarks(token.getValue(), type, pageable);

            model.addAttribute("type", type);
            model.addAttribute("nowPage", nowPage);
            model.addAttribute("endPage", data.getTotalPages());
            model.addAttribute("posts", data);
            return "/user/mypage/myBookmarks";

        } catch (Exception e) {
            log.error("북마크 불러오기 실패: {}", e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "/error/errorPage";
        }
    }


}
