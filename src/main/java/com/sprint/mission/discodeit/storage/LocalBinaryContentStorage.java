package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binary.BinaryContentResponse;
import com.sprint.mission.discodeit.exception.binary.BinaryContentException;
import com.sprint.mission.discodeit.exception.enums.BinaryContentErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalBinaryContentStorage implements BinaryContentStorage {

  @Value("${discodeit.storage.local.root-path:.discodeit}")
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
  public UUID put(UUID id, byte[] bytes) {
    Path filePath = resolvePath(id);
    try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
      fos.write(bytes);
      log.debug("파일 저장 : id={}", id);
      return id;
    } catch (IOException e) {
      log.error("파일 저장 실패 : id={}", id, e);
      throw new BinaryContentException(BinaryContentErrorCode.FILE_UPLOAD_FAILED, e.getMessage());
    }
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
    } catch (Exception e) {
      log.error("파일 다운로드 실패 : id={}", binaryContent.id(), e);
      return ResponseEntity.internalServerError().build();
    }
  }

  private Path resolvePath(UUID id) {
    return rootPath.resolve(id.toString());
  }
}
