package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binary.BinaryContentResponse;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.binary.BinaryContentException;
import com.sprint.mission.discodeit.exception.enums.BinaryContentErrorCode;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalBinaryContentStorage implements BinaryContentStorage {

  @Value("${discodeit.storage.local.root-path:.discodeit/storage}")
  private String rootPathStr;

  private Path rootPath;

  @PostConstruct
  public void init() {
    try {
      this.rootPath = Paths.get(rootPathStr);
      if (!Files.exists(rootPath)) {
        Files.createDirectories(rootPath);
        log.info("생성된 로컬 저장소 디렉토리: {}", rootPath.toAbsolutePath());
      } else {
        log.info("존재하는 로컬 저장소 디렉토리: {}", rootPath.toAbsolutePath());
      }
    } catch (IOException e) {
      log.error("로컬 저장소 초기화 실패", e.getMessage());
      throw new RuntimeException("로컬 저장소 초기화 실패", e);
    }
  }

  @Override
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
  public UUID put(UUID id, byte[] bytes) {

    log.info("[파일 업로드 시도] id = {}", id);

    Path filePath = resolvePath(id);

    try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
      fos.write(bytes);
      Thread.sleep(3000);
      log.debug("파일 저장 : id={}", id);
      return id;
    } catch (IOException e) {
      log.error("파일 저장 실패 : id={}", id, e);
      throw new BinaryContentException(BinaryContentErrorCode.FILE_UPLOAD_FAILED, e.getMessage());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new DiscodeitException(BinaryContentErrorCode.FILE_UPLOAD_FAILED, e.getMessage());
    }
  }

  @Recover
  public UUID recover(Exception e, UUID id, byte[] bytes) {

    // MDC에서 Request ID 가져오기
    String requestId = MDC.get("request_id");
    if (requestId == null) {
      requestId = "N/A";
    }

    // 관리자 통지 로그
    log.error("""
        [관리자 통지: 파일 업로드 최종 실패]
        RequestId: {}
        BinaryContentId: {}
        Error: {}
        """, requestId, id, e.getMessage());

    // 예외 처리
    throw new DiscodeitException(BinaryContentErrorCode.FILE_UPLOAD_FAILED, e.getMessage());
  }

  @Override
  public InputStream get(UUID id) {
    Path filePath = resolvePath(id);
    try {
      log.debug("파일 로드 : id={}", id);
      return new FileInputStream(filePath.toFile());
    } catch (FileNotFoundException e) {
      log.error("파일을 찾을 수 없음 : id={}", id, e);
      throw new BinaryContentException(BinaryContentErrorCode.FILE_NOT_FOUND, e.getMessage());
    }
  }

  @Override
  public ResponseEntity<?> download(BinaryContentResponse binaryContent) {
    try {
      InputStream inputStream = get(binaryContent.id());
      Resource resource = new InputStreamResource(inputStream);

      String contentDisposition = "attachment; filename=\"" + binaryContent.fileName() + "\"";

      log.info("파일 다운로드 응답 : id={}", binaryContent.id());
      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(binaryContent.contentType()))
          .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
          .body(resource);
    } catch (BinaryContentException e) {
      throw e;
    } catch (Exception e) {
      log.error("파일 다운로드 실패 : id={}", binaryContent.id(), e);
      throw new BinaryContentException(BinaryContentErrorCode.FILE_DOWNLOAD_FAILED, e.getMessage());
    }
  }

  private Path resolvePath(UUID id) {
    return rootPath.resolve(id.toString());
  }
}
