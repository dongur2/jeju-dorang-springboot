package com.donguri.jejudorang.domain.community.api;

import com.donguri.jejudorang.global.common.s3.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


/*
* Community Toast 에디터 이미지 첨부 - S3
*
* */
@Slf4j
@RestController
@RequestMapping("/tui-editor")
public class CommunityImageFileController {

    private final ImageService imageService;
    public CommunityImageFileController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/img-upload")
    public String uploadEditorImage(@RequestParam("image") final MultipartFile image) {

        try {
            if (image.isEmpty()) {
                throw new BadRequestException("첨부된 이미지가 없습니다.");

            } else if (image.getSize() > 3000000) {
                throw new IllegalAccessException("파일 크기는 3MB를 초과할 수 없습니다");
            }

            Map<String, String> resultMap = imageService.uploadImg(image);
            return resultMap.get("imgUrl");

        } catch (Exception e) {
            log.error("이미지 첨부 실패: {}", e.getMessage());
            return e.getMessage();
        }
    }

}
