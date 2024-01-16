package com.donguri.jejudorang.domain.community.api;

import com.donguri.jejudorang.domain.community.dto.request.CommunityUpdateRequestDto;
import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityForModifyResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityTypeResponseDto;
import com.donguri.jejudorang.domain.community.service.CommunityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequestMapping("/community")
public class CommunityController {
    @Autowired
    private CommunityService communityService;

    @Value("${kakao-api-key}")
    private String kakaoApiKey;


    // default: api package 내에서만 사용 가능 - getPartyList, getCharList
    static String convertToProperty(String order) {
        if (order.equals("recent")) {
            order = "createdAt";
        } else if (order.equals("comment")) {
            order = "comments";
        } else if (order.equals("liked")) {
            order = "liked";
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
    public String postNewCommunity(CommunityWriteRequestDto postToWrite, Model model) {
        CommunityTypeResponseDto communityTypeResponseDto = communityService.saveNewPost(postToWrite);

        return "redirect:/community/" + communityTypeResponseDto.getTypeForRedirect();
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
    public String modifyCommunity(@PathVariable("communityId") Long communityId, CommunityUpdateRequestDto postToUpdate) {
        CommunityTypeResponseDto redirectTypeDto = communityService.updatePost(communityId, postToUpdate);
        return "redirect:/community/" + redirectTypeDto.getTypeForRedirect() + "/{communityId}";
    }


}
