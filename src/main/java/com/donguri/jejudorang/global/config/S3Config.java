package com.donguri.jejudorang.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;


@Slf4j
@Configuration
public class S3Config {
    @Value("${aws.credentials.accessKey}") private String accessKey;
    @Value("${aws.credentials.secretKey}") private String secretKey;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        try {
            log.info("S3 서비스 클라이언트 생성 시작");
            return S3Client
                    .builder()
                    .region(Region.AP_NORTHEAST_2)
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();

        } catch (S3Exception e) {
            log.error("S3 서비스 클라이언트 생성에 실패했습니다 : {}", e.getMessage());
            return null;
        }
    }

}
