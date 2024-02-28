package com.donguri.jejudorang.global.common.s3;

import com.donguri.jejudorang.global.error.CustomErrorCode;
import com.donguri.jejudorang.global.error.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class ImageServiceI implements ImageService {

    private final String bucketName;
    @Autowired private final S3Client s3Client;

    public ImageServiceI(@Value("${aws.s3.bucket.name}") String bucketName, S3Client s3Client) {
        this.bucketName = bucketName;
        this.s3Client = s3Client;
    }

    @Override
    @Transactional
    public Map<String, String> uploadImg(MultipartFile imgFile) {
        try {
            String originalName = imgFile.getOriginalFilename();
            log.info("이미지 파일의 사용자 저장 이름 : {}", originalName);
            String fileType = originalName.substring(originalName.length() - 4).toLowerCase();
            log.info("파일 확장자 : {}", fileType);

            if (!(fileType.contains("png") || fileType.contains("jpg") || fileType.contains("jpeg"))) {
                throw new CustomException(CustomErrorCode.NOT_SUPPORTED_TYPE_FOR_IMAGE);
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

            Map<String, String> result = new HashMap<>();
            result.put("imgName", objectKey);
            result.put("imgUrl", url.toString());

            return result;

        } catch (S3Exception e) {
            log.error("사진 업로드 실패: S3 통신 오류 {}", e.getMessage());
            System.exit(1);
            throw e;

        } catch (CustomException e) {
            log.error("사진 업로드 실패: {}", e.getCustomErrorCode().getMessage());
            throw e;

        } catch (IOException e) {
            log.error("사진 업로드 실패");
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void deleteImg(String objectName) {
        ArrayList<ObjectIdentifier> toDelete = new ArrayList<>();
        toDelete.add(ObjectIdentifier.builder()
                .key(objectName)
                .build());

        try {
            DeleteObjectsRequest dor = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(Delete.builder()
                            .objects(toDelete).build())
                    .build();

            s3Client.deleteObjects(dor);

        } catch (S3Exception e) {
            log.error("S3의 이미지 삭제 실패 : {}", e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        log.info("S3의 이미지 삭제 완료");
    }
}
