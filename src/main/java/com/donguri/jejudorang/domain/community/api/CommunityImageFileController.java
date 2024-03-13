package com.donguri.jejudorang.domain.community.api;

import com.donguri.jejudorang.domain.community.api.swagger.CommunityImageFileControllerDocs;
import com.donguri.jejudorang.global.common.s3.ImageService;
import com.donguri.jejudorang.global.error.CustomErrorCode;
import com.donguri.jejudorang.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


/*
* Community Toast 에디터 이미지 첨부 - S3
*
* */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/tui-editor")
public class CommunityImageFileController implements CommunityImageFileControllerDocs {
    private final ImageService imageService;

    @PostMapping("/img-upload")
    public String uploadEditorImage(@RequestParam("image") final MultipartFile image) {

        try {
            if (image.isEmpty()) {
                throw new CustomException(CustomErrorCode.NO_REQUEST_IMAGE);

            } else if (image.getSize() > 3000000) {
                throw new CustomException(CustomErrorCode.IMAGE_TOO_LARGE_FOR_COMMUNITY);
            }

            Map<String, String> resultMap = imageService.uploadImg(image);
            return resultMap.get("imgUrl");

        } catch (CustomException e) {
            log.error("이미지 첨부 실패: {}", e.getCustomErrorCode().getMessage());
            return e.getCustomErrorCode().getMessage();

        } catch (Exception e) {
            log.error("이미지 첨부 실패: [서버 오류] {}", e.getMessage());
            return e.getMessage();
        }
    }

}
