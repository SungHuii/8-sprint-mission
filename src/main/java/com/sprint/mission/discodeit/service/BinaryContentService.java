package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.enums.BinaryContentStatus;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  // create
  BinaryContent create(BinaryContentCreateRequest request);

  // find
  BinaryContent findById(UUID binaryContentId);

  List<BinaryContent> findAllByIdIn(List<UUID> ids);

  // delete
  void deleteById(UUID binaryContentId);

  // update status
  BinaryContent updateStatus(UUID binaryContentId, BinaryContentStatus status);
}