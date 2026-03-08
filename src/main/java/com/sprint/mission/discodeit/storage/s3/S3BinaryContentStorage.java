package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.binary.BinaryContentResponse;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3Properties s3Properties;

  public S3BinaryContentStorage(S3Properties s3Properties) {
    this.s3Properties = s3Properties;
  }

  private S3Client getS3Client() {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(
        s3Properties.accessKey(),
        s3Properties.secretKey()
    );

    return S3Client.builder()
        .region(Region.of(s3Properties.region()))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();
  }

  // S3 업로드
  @Override
  public UUID put(UUID id, byte[] bytes) {
    S3Client s3Client = getS3Client();

    try {
      PutObjectRequest putRequest = PutObjectRequest.builder()
          .bucket(s3Properties.bucket())
          .key(id.toString())
          .build();

      s3Client.putObject(putRequest, RequestBody.fromBytes(bytes));
      return id;
    } finally {
      s3Client.close();
    }
  }

  // S3에서 InputStream 반환
  @Override
  public InputStream get(UUID id) {
    S3Client s3Client = getS3Client();

    try {
      GetObjectRequest getRequest = GetObjectRequest.builder()
          .bucket(s3Properties.bucket())
          .key(id.toString())
          .build();

      ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getRequest);
      return new ByteArrayInputStream(objectBytes.asByteArray());
    } finally {
      s3Client.close();
    }
  }

  @Override
  public ResponseEntity<?> download(BinaryContentResponse binaryContent) {
    String presignedUrl = generatePresignedUrl(
        binaryContent.id().toString(),
        binaryContent.contentType()
    );

    return ResponseEntity
        .status(HttpStatus.FOUND)
        .header(HttpHeaders.LOCATION, presignedUrl)
        .build();
  }

  // generatePresignedUrl 메소드
  private String generatePresignedUrl(String key, String contentType) {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(
        s3Properties.accessKey(),
        s3Properties.secretKey()
    );

    S3Presigner presigner = S3Presigner.builder()
        .region(Region.of(s3Properties.region()))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    try {
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(s3Properties.bucket())
          .key(key)
          .responseContentType(contentType)
          .build();

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofSeconds(s3Properties.presignedUrlExpiration()))
          .getObjectRequest(getObjectRequest)
          .build();

      PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
      return presignedRequest.url().toString();
    } finally {
      presigner.close();
    }
  }
}
