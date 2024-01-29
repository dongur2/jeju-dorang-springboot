package com.donguri.jejudorang.domain.user.service.s3;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    void putS3Object(MultipartFile img);
}
