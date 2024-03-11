package com.donguri.jejudorang.global.common.s3;

import com.donguri.jejudorang.global.error.CustomErrorCode;
import com.donguri.jejudorang.global.error.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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
            String fileType = originalName.substring(originalName.length() - 4).toLowerCase();

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

            GetUrlRequest request = GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            URL url = s3Client.utilities().getUrl(request);

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
    }

    @Override
    @Transactional
    public void deleteOrphanedImagesWithNames(List<String> profileImgNames, List<String> communityImgNames) {
       try {
           // DB에서 사용하고 있는 이미지 이름 리스트: 프로필 사진 이름 + 커뮤니티 게시글 첨부 이미지 이름
           List<String> imgNameList = new ArrayList<>(profileImgNames);
           imgNameList.addAll(communityImgNames);

           // S3 bucket에 저장된 이미지 이름 리스트
           List<String> imgNamesFromS3 = new ArrayList<>();
           imgNameList.forEach(img -> {
               ListObjectsRequest objectRequest = ListObjectsRequest.builder()
                       .bucket(bucketName)
                       .build();

               ListObjectsResponse listResponse = s3Client.listObjects(objectRequest);
               imgNamesFromS3.addAll(listResponse.contents()
                       .stream().map(S3Object::key)
                       .toList());
           });

           // 사용하고 있는 이미지가 있을 경우 & 버킷에 이미지가 존재할 경우
           if (!imgNameList.isEmpty() && !imgNamesFromS3.isEmpty()) {
               imgNamesFromS3.forEach(name -> {
                   if (!imgNameList.contains(name)) {
                       deleteImg(name);
                   }
               });

           // 사용 이미지가 없을 경우: 버킷 이미지 전체 삭제
           } else if (imgNameList.isEmpty()) {
               imgNamesFromS3.forEach(this::deleteImg);
           } else { // 버킷이 비어있을 경우
               log.info("S3 bucket이 비어있습니다.");
           }

       } catch (S3Exception e) {
           log.error("버킷에서 이미지 삭제 실패 {}", e.awsErrorDetails().errorMessage());
           System.exit(1);
       } catch (Exception e) {
           log.error("이미지 삭제 실패: {}", e.getMessage());
           throw e;
       }
    }
}
