package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.binary.BinaryContentException;
import com.sprint.mission.discodeit.exception.enums.BinaryContentErrorCode;
import com.sprint.mission.discodeit.exception.enums.CommonErrorCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public BinaryContent create(BinaryContentCreateRequest request) {
    validateCreateRequest(request);
    log.info("바이너리 컨텐츠 생성 요청: fileName={}, size={}", request.fileName(), request.size());

    // 1. 메타 데이터 저장용 엔티티 생성
    UUID id = UUID.randomUUID();
    BinaryContent content = new BinaryContent(
        request.contentType(),
        request.fileName(),
        request.size()
    );
    BinaryContent saved = binaryContentRepository.save(content);

    // 2. 실제 파일 저장 (Storage)
    binaryContentStorage.put(saved.getId(), request.bytes());

    log.info("바이너리 컨텐츠 생성 완료: id={}", saved.getId());
    return saved;
  }

  @Override
  public BinaryContent findById(UUID binaryContentId) {
    if (binaryContentId == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "binaryContentId는 필수입니다.");
    }
    log.debug("바이너리 컨텐츠 조회 요청: id={}", binaryContentId);

    return binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new BinaryContentException(BinaryContentErrorCode.FILE_NOT_FOUND));
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "ids는 필수이며 비어 있을 수 없습니다.");
    }
    log.debug("바이너리 컨텐츠 목록 조회 요청: ids={}", ids);

    return binaryContentRepository.findAllByIdIn(ids);
  }

  @Override
  @Transactional
  public void deleteById(UUID binaryContentId) {
    if (binaryContentId == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "binaryContentId는 필수입니다.");
    }
    log.info("바이너리 컨텐츠 삭제 요청: id={}", binaryContentId);

    binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new BinaryContentException(BinaryContentErrorCode.FILE_NOT_FOUND));

    binaryContentRepository.deleteById(binaryContentId);
  }

  private void validateCreateRequest(BinaryContentCreateRequest request) {
    if (request == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "요청이 null입니다.");
    }
    if (request.bytes() == null || request.bytes().length == 0) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "bytes는 필수입니다.");
    }
    if (request.contentType() == null || request.contentType().isBlank()) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "contentType은 필수입니다.");
    }
    if (request.fileName() == null || request.fileName().isBlank()) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "fileName은 필수입니다.");
    }
  }
}
