package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class AWSS3Test {

    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private String bucketName;
    private String accessKey;
    private String secretKey;
    private String region;

    @BeforeEach
    void setUp() throws IOException {
        // Properties 클래스를 활용해서 .env에 정의한 AWS 정보를 로드
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(".env")) {
            if (input != null) {
                properties.load(input);
            }
        }

        // 환경변수에서 AWS 정보 로드 (Properties에서 못 찾으면 시스템 환경변수 사용)
        accessKey = properties.getProperty("AWS_S3_ACCESS_KEY", System.getenv("AWS_S3_ACCESS_KEY"));
        secretKey = properties.getProperty("AWS_S3_SECRET_KEY", System.getenv("AWS_S3_SECRET_KEY"));
        region = properties.getProperty("AWS_S3_REGION", System.getenv("AWS_S3_REGION"));
        bucketName = properties.getProperty("AWS_S3_BUCKET", System.getenv("AWS_S3_BUCKET"));

        // AWS Credentials 설정
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // S3Client 초기화
        s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        // S3Presigner 초기화
        s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Test
    @DisplayName("S3 업로드 테스트")
    @EnabledIfEnvironmentVariable(named = "AWS_S3_ACCESS_KEY", matches = ".+")
    void testUpload() {
        // given
        String key = "test-upload-file.txt";
        String content = "Hello AWS S3!";
        byte[] contentBytes = content.getBytes();

        // when
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("text/plain")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(contentBytes));

        // then
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);

        assertThat(headObjectResponse.contentLength()).isEqualTo(contentBytes.length);
        assertThat(headObjectResponse.contentType()).isEqualTo("text/plain");

        // cleanup
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(deleteRequest);
    }

    @Test
    @DisplayName("S3 다운로드 테스트")
    @EnabledIfEnvironmentVariable(named = "AWS_S3_ACCESS_KEY", matches = ".+")
    void testDownload() {
        // given
        String key = "test-download-file.txt";
        String content = "Download Test Content";
        byte[] contentBytes = content.getBytes();

        // 먼저 파일 업로드
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("text/plain")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(contentBytes));

        // when - 다운로드
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);

        // then
        String downloadedContent = objectBytes.asUtf8String();
        assertThat(downloadedContent).isEqualTo(content);
        assertThat(objectBytes.response().contentType()).isEqualTo("text/plain");

        // cleanup
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(deleteRequest);
    }

    @Test
    @DisplayName("PresignedUrl 생성 테스트")
    @EnabledIfEnvironmentVariable(named = "AWS_S3_ACCESS_KEY", matches = ".+")
    void testGeneratePresignedUrl() {
        // given
        String key = "test-presigned-url-file.txt";
        String content = "Presigned URL Test Content";
        byte[] contentBytes = content.getBytes();

        // 먼저 파일 업로드
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("text/plain")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(contentBytes));

        // when - Presigned URL 생성
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        // then
        assertThat(presignedRequest.url()).isNotNull();
        assertThat(presignedRequest.url().toString()).contains(bucketName);
        assertThat(presignedRequest.url().toString()).contains(key);
        assertThat(presignedRequest.url().toString()).contains("X-Amz-Signature");

        System.out.println("Generated Presigned URL: " + presignedRequest.url());

        // cleanup
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(deleteRequest);
    }
}
