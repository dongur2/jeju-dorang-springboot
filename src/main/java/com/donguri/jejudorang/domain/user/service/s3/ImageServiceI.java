package com.donguri.jejudorang.domain.user.service.s3;

import com.donguri.jejudorang.domain.user.repository.ProfileRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

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
    public String putS3Object(MultipartFile imgFile) {
        try {
            if (imgFile.getSize() > 1000000) {
                throw new IllegalAccessException("파일 크기는 1MB를 초과할 수 없습니다");
            }

            String originalName = imgFile.getOriginalFilename();
            log.info("이미지 파일의 사용자 저장 이름 : {}", originalName);
            String fileType = originalName.substring(originalName.length() - 4);
            log.info("파일 확장자 : {}", fileType);

            if (!(fileType.contains("png") || fileType.contains("jpg") || fileType.contains("jpeg")
                || fileType.contains("PNG") || fileType.contains("JPG") || fileType.contains("JPEG"))) {

                throw new IllegalAccessException("파일은 png, jpg, jpeg만 가능합니다");
            }

            UUID uuid = UUID.randomUUID();
            String objectKey = uuid + originalName;

            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(imgFile.getContentType())
                    .contentLength(imgFile.getSize())
                    .build();

            s3Client.putObject(putOb, RequestBody.fromBytes(imgFile.getBytes()));
            log.info(" ** S3에 사진 업로드 완료 ** name: {}", objectKey);

            GetUrlRequest request = GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            URL url = s3Client.utilities().getUrl(request);
            log.info(" ** S3에 저장된 사진 URL 불러오기 완료 ** ");
            log.info("The URL for {} is {}", objectKey, url);
            return url.toString();

        } catch (S3Exception e) {
            log.error("사진 업로드 실패: S3 통신 오류 {}", e.getMessage());
            System.exit(1);
            return null;

        } catch (IllegalAccessException e) {
            log.error("사진 업로드 실패: {}", e.getMessage());
            return null;

        } catch (IOException e) {
            log.error("사진 업로드 실패");
            throw new RuntimeException(e);
        }
    }
}
