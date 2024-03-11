package com.donguri.jejudorang.global.common.s3;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ImageService {

    // 이미지 업로드
    Map<String, String> uploadImg(MultipartFile img);

    // 이미지 삭제
    void deleteImg(String objectName);

    void deleteOrphanedImagesWithNames(List<String> profileImgNames, List<String> communityImgNames);
}
