package com.donguri.jejudorang.domain.community.api;

import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityForModifyResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityTypeResponseDto;
import com.donguri.jejudorang.domain.community.service.CommunityService;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;


@Slf4j
@Controller
@RequestMapping("/community")
public class CommunityController {

    private final String kakaoApiKey;

    @Autowired private final CommunityService communityService;

    public CommunityController(CommunityService communityService, @Value("${kakao-api-key}") String kakaoApiKey) {
        this.communityService = communityService;
        this.kakaoApiKey = kakaoApiKey;
    }


    // default: api package 내에서만 사용 가능 - getPartyList, getCharList
    static String convertToProperty(String order) {
        if (order.equals("recent")) {
            order = "createdAt";
        } else if (order.equals("comment")) {
            order = "comments";
        } else if (order.equals("bookmark")) {
            order = "bookmarksCount";
        }
        return order;
    }

    /*
    * 글 작성
    * /community/post/new
    * GET, POST
    *
    * */
    @GetMapping("/post/new")
    public String getCommunityWriteForm(@RequestParam(name = "type") String preType, Model model) {
        model.addAttribute("type", preType); // 미리 설정되는 글타입
        return "/community/communityPostForm";
    }

    @PostMapping("/post/new")
    public String postNewCommunity(@Valid CommunityWriteRequestDto postToWrite, BindingResult bindingResult,
                                   @CookieValue("access_token") Cookie token,
                                   Model model) {
        // 유효성 검사 에러
        if (bindingResult.hasErrors()) {
            return bindErrorPage(bindingResult, model);
        }

        try {
            CommunityTypeResponseDto communityTypeResponseDto = communityService.saveNewPost(postToWrite);
            return "redirect:/community/" + communityTypeResponseDto.typeForRedirect();

        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "/error/errorTemp";
        }
    }


    /*
    * 글 수정
    * /community/post/{communityId}/modify
    * GET, PUT
    *
    * */
    @GetMapping("/post/{communityId}/modify")
    public String getCommunityModifyForm(@PathVariable("communityId") Long communityId, Model model) {
        CommunityForModifyResponseDto foundPost = communityService.getCommunityPost(communityId);
        model.addAttribute("post", foundPost);
        return "/community/communityModifyForm";
    }

    @PutMapping("/post/{communityId}/modify")
    public String modifyCommunity(@PathVariable("communityId") Long communityId,
                                  @Valid CommunityWriteRequestDto postToUpdate,
                                  BindingResult bindingResult,
                                  Model model) {
        // 유효성 검사 에러
        if (bindingResult.hasErrors()) {
            return bindErrorPage(bindingResult, model);
        }

        try {
            CommunityTypeResponseDto redirectTypeDto = communityService.updatePost(communityId, postToUpdate);
            return "redirect:/community/" + redirectTypeDto.typeForRedirect() + "/{communityId}";

        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "/error/errorTemp";
        }
    }

    private static String bindErrorPage(BindingResult bindingResult, Model model) {
        model.addAttribute("errorMsg", bindingResult.getFieldError().getDefaultMessage());
        return "/error/errorTemp";
    }


}
