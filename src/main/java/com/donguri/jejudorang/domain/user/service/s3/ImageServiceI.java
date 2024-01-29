package com.donguri.jejudorang.domain.user.service.s3;

import com.donguri.jejudorang.domain.user.repository.ProfileRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ImageServiceI implements ImageService {

    private final String bucketName;
    @Autowired private final S3Client s3Client;
    @Autowired private final UserRepository userRepository;
    @Autowired private final ProfileRepository profileRepository;

    public ImageServiceI(@Value("${aws.s3.bucket}") String bucketName, S3Client s3Client, UserRepository userRepository, ProfileRepository profileRepository) {
        this.bucketName = bucketName;
        this.s3Client = s3Client;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public void putS3Object(MultipartFile imgFile) {
        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(imgFile.getOriginalFilename())
                    .contentType(imgFile.getContentType())
                    .contentLength(imgFile.getSize())
                    .build();

            PutObjectResponse putObjectResponse = s3Client.putObject(putOb, RequestBody.fromBytes(imgFile.getBytes()));
            log.info(" ** S3에 사진 업로드 완료 ** ");
            log.info("Successfully placed {} into bucket {}", imgFile.getOriginalFilename(), bucketName);

        } catch (S3Exception e) {
            log.error("사진 업로드 실패: S3 통신 오류 {}", e.getMessage());
            System.exit(1);

        } catch (IOException e) {
            log.error("사진 업로드 실패");
            throw new RuntimeException(e);
        }
    }
}
