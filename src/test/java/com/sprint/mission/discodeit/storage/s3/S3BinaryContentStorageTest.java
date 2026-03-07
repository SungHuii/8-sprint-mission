package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.binary.BinaryContentResponse;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@EnabledIfEnvironmentVariable(named = "AWS_S3_ACCESS_KEY", matches = ".+")
public class S3BinaryContentStorageTest {

  private S3BinaryContentStorage storage;

  // 환경변수에서 가져옴
  private String accessKey;
  private String secretKey;
  private String region;
  private String bucket;
  private int presignedUrlExpiration;

  @BeforeEach
  void setUp() {
    // 환경변수 로드
    accessKey = System.getenv("AWS_S3_ACCESS_KEY");
    secretKey = System.getenv("AWS_S3_SECRET_KEY");
    region = System.getenv("AWS_S3_REGION");
    bucket = System.getenv("AWS_S3_BUCKET");
    presignedUrlExpiration = 600;

    // S3BinaryContentStorage 초기화
    storage = new S3BinaryContentStorage(
        accessKey,
        secretKey,
        region,
        bucket,
        presignedUrlExpiration
    );
  }

  @Test
  @DisplayName("S3에 파일 업로드 테스트")
  void testPut() {
    // given
    UUID id = UUID.randomUUID();
    String content = "S3BinaryContentStorage 테스트 컨텐츠";
    byte[] bytes = content.getBytes();

    // when
    UUID resultId = storage.put(id, bytes);

    // then
    assert resultId.equals(id);

    // cleanup
    deleteFromS3(id);
  }

  @Test
  @DisplayName("S3에서 파일 다운로드 테스트")
  void testGet() throws Exception {
    // given (파일 업로드)
    UUID id = UUID.randomUUID();
    String content = "파일 다운로드 테스트";
    byte[] bytes = content.getBytes();
    storage.put(id, bytes);

    // when
    InputStream inputStream = storage.get(id);

    // then
    assertThat(inputStream).isNotNull();
    String downloadedContent = new String(inputStream.readAllBytes());
    assertThat(downloadedContent).isEqualTo(content);

    inputStream.close();

    // cleanup
    deleteFromS3(id);
  }

  @Test
  @DisplayName("S3 Presigned URL 생성, Redirect 테스트")
  void testDownload() {
    // given (파일 업로드)
    UUID id = UUID.randomUUID();
    String content = "테스트 컨텐츠 다운로드";
    byte[] bytes = content.getBytes();
    storage.put(id, bytes);

    BinaryContentResponse binaryContentResponse = new BinaryContentResponse(
        id,
        "test-file.txt",
        bytes.length,
        "text/plain"
    );

    // when
    ResponseEntity<?> response = storage.download(binaryContentResponse);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    assertThat(response.getHeaders().getLocation()).isNotNull();
    assertThat(response.getHeaders().getLocation().toString())
        .contains(bucket)
        .contains(id.toString())
        .contains("X-Amz-Signature");

    System.out.println("Presigned URL: " + response.getHeaders().getLocation());

    // cleanup
    deleteFromS3(id);
  }

  private void deleteFromS3(UUID id) {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

    S3Client s3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    try {
      s3Client.deleteObject(builder -> builder
          .bucket(bucket)
          .key(id.toString()));
    } finally {
      s3Client.close();
    }
  }

}
