package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.integration.support.IntegrationTestSupport;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

class BinaryContentIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private BinaryContentRepository binaryContentRepository;

  @Autowired
  private BinaryContentStorage binaryContentStorage;

  @Test
  @DisplayName("파일 다운로드 성공")
  void download_Success() throws Exception {
    // given
    String fileName = "test.txt";
    String contentType = "text/plain";
    byte[] content = "Hello World".getBytes();

    // 1. DB 저장
    BinaryContent binaryContent = new BinaryContent(contentType, fileName, (long) content.length);
    binaryContentRepository.save(binaryContent);
    UUID fileId = binaryContent.getId();

    // 2. Storage 저장 (실제 파일 생성)
    binaryContentStorage.put(fileId, content);

    // when & then
    mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", fileId))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType))
        .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + fileName + "\""));
  }

  @Test
  @DisplayName("파일 다운로드 실패 - 존재하지 않는 파일")
  void download_Fail_FileNotFound() throws Exception {
    // given
    UUID unknownId = UUID.randomUUID();

    // when & then
    mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", unknownId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("FILE-NOT_FOUND"));
  }
}
