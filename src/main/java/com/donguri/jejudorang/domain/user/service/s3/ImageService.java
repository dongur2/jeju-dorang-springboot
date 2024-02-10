package com.donguri.jejudorang.domain.user.service.s3;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ImageService {

    // 이미지 업로드
    Map<String, String> uploadImg(MultipartFile img);

    // 이미지 삭제
    void deleteImg(String objectName);
}
