package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binary.BinaryContentException;
import com.sprint.mission.discodeit.exception.enums.BinaryContentErrorCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BinaryContentServiceTest {

  @InjectMocks
  private BasicBinaryContentService binaryContentService;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @Test
  @DisplayName("바이너리 컨텐츠 생성 성공")
  void create_Success() {
    // given
    String fileName = "test.txt";
    long size = 1024L;
    String contentType = "text/plain";
    byte[] bytes = "content".getBytes();
    BinaryContentCreateRequest request = new BinaryContentCreateRequest(fileName, size, contentType,
        bytes);

    BinaryContent binaryContent = new BinaryContent(contentType, fileName, size);

    given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);

    // when
    BinaryContent result = binaryContentService.create(request);

    // then
    assertThat(result.getOriginalName()).isEqualTo(fileName);
    verify(binaryContentRepository).save(any(BinaryContent.class));
    verify(binaryContentStorage).put(any(), any()); // Storage 저장 호출 확인
  }

  @Test
  @DisplayName("바이너리 컨텐츠 조회 성공")
  void findById_Success() {
    // given
    UUID id = UUID.randomUUID();
    BinaryContent binaryContent = new BinaryContent("text/plain", "test.txt", 1024L);

    given(binaryContentRepository.findById(id)).willReturn(Optional.of(binaryContent));

    // when
    BinaryContent result = binaryContentService.findById(id);

    // then
    assertThat(result.getOriginalName()).isEqualTo("test.txt");
  }

  @Test
  @DisplayName("바이너리 컨텐츠 조회 실패 - 존재하지 않는 ID")
  void findById_Fail_NotFound() {
    // given
    UUID id = UUID.randomUUID();
    given(binaryContentRepository.findById(id)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> binaryContentService.findById(id))
        .isInstanceOf(BinaryContentException.class)
        .extracting(e -> ((BinaryContentException) e).getErrorCode())
        .isEqualTo(BinaryContentErrorCode.FILE_NOT_FOUND);
  }

  @Test
  @DisplayName("바이너리 컨텐츠 삭제 성공")
  void deleteById_Success() {
    // given
    UUID id = UUID.randomUUID();
    BinaryContent binaryContent = new BinaryContent("text/plain", "test.txt", 1024L);

    given(binaryContentRepository.findById(id)).willReturn(Optional.of(binaryContent));

    // when
    binaryContentService.deleteById(id);

    // then
    verify(binaryContentRepository).deleteById(id);
  }

  @Test
  @DisplayName("바이너리 컨텐츠 삭제 실패 - 존재하지 않는 ID")
  void deleteById_Fail_NotFound() {
    // given
    UUID id = UUID.randomUUID();
    given(binaryContentRepository.findById(id)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> binaryContentService.deleteById(id))
        .isInstanceOf(BinaryContentException.class)
        .extracting(e -> ((BinaryContentException) e).getErrorCode())
        .isEqualTo(BinaryContentErrorCode.FILE_NOT_FOUND);
  }
}
