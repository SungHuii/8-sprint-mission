package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  // create
  ReadStatusResponse create(ReadStatusCreateRequest request);

  // find
  ReadStatusResponse findById(UUID readStatusId);

  List<ReadStatusResponse> findAllByUserId(UUID userId);

  // update
  ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request);

  // delete
  void deleteById(UUID readStatusId);
}
