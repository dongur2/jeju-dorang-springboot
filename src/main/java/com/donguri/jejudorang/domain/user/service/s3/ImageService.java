package com.donguri.jejudorang.domain.user.service.s3;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String putS3Object(MultipartFile img);
}
