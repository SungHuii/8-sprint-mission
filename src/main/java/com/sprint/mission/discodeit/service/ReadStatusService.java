package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    // create
    ReadStatusResponse create(ReadStatusCreateRequest request);

    // find
    ReadStatusResponse findById(UUID readStatusId);
    List<ReadStatusResponse> findAllByUserId(UUID userId);

    // update
    ReadStatusResponse update(ReadStatusUpdateRequest request);

    // delete
    void deleteById(UUID readStatusId);
}
