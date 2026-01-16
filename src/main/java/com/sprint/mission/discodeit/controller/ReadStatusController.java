package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.docs.ReadStatusApi;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdatePayload;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;

  @Override
  @PostMapping
  public ResponseEntity<ReadStatusResponse> create(@RequestBody ReadStatusCreateRequest request) {
    return ResponseEntity.ok(readStatusService.create(request));
  }

  @Override
  @GetMapping
  public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(@RequestParam UUID userId) {
    return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
  }

  @Override
  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatusResponse> update(@PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdatePayload payload) {
    Instant lastReadAt = payload.newLastReadAt() != null
        ? Instant.parse(payload.newLastReadAt())
        : Instant.now();

    ReadStatusUpdateRequest newRequest = new ReadStatusUpdateRequest(
        readStatusId,
        lastReadAt
    );
    return ResponseEntity.ok(readStatusService.update(newRequest));
  }
}
