package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.docs.ReadStatusApi;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;

  @Override
  @PostMapping
  public ResponseEntity<ReadStatusResponse> create(
      @Valid @RequestBody ReadStatusCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(readStatusService.create(request));
  }

  @Override
  @GetMapping
  public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(@RequestParam UUID userId) {
    return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
  }

  @Override
  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatusResponse> update(@PathVariable UUID readStatusId,
      @Valid @RequestBody ReadStatusUpdateRequest request) {
    return ResponseEntity.ok(readStatusService.update(readStatusId, request));
  }
}
