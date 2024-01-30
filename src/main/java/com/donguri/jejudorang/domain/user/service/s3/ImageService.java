package com.donguri.jejudorang.domain.user.service.s3;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ImageService {
    Map<String, String> putS3Object(MultipartFile img);

    void deleteS3Object(String objectName);
}
