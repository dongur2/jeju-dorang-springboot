package com.donguri.jejudorang.domain.user.service;

import com.donguri.jejudorang.domain.community.service.CommunityService;
import com.donguri.jejudorang.global.auth.jwt.JwtProvider;
import com.donguri.jejudorang.global.common.s3.ImageService;
import com.donguri.jejudorang.global.error.CustomErrorCode;
import com.donguri.jejudorang.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceI implements AdminService {
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final ImageService imageService;
    private final CommunityService communityService;


    @Override
    @Transactional
    public void checkIsAdmin(String token) {
        if(!jwtProvider.getAuthoritiesFromJWT(token).get(0).getAuthority().equals("ADMIN")
                || !jwtProvider.validateJwtToken(token)) {
            throw new CustomException(CustomErrorCode.PERMISSION_ERROR);
        }
    }

    @Override
    @Transactional
    public void deleteUnusedImages(String token) {
        // 권한 제한
        checkIsAdmin(token);

        // 1. 사용중인 이미지 확인을 위해 Profile.imgName, Community.content의 이미지 이름 리스트 추출
        List<String> profileImgNames = userService.getAllUsersProfileImageNames()
                .stream().filter(name -> !name.equals("default-img.png"))
                .toList();
        List<String> allContents = communityService.getAllContents().stream()
                .map(content -> {
                    int startIndex = content.indexOf(".amazonaws.com/") + ".amazonaws.com/".length();
                    int endIndex = content.indexOf(" alt", startIndex) + " alt".length() - 5;
                    return content.substring(startIndex, endIndex).trim();
                }).toList();

        // 2. 가져온 사용중 이미지 리스트를 버킷에 있는 이미지들의 이름과 비교해서, 존재하지 않는 파일은 삭제
        imageService.deleteOrphanedImagesWithNames(profileImgNames, allContents);
    }

}
