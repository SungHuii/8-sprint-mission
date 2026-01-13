package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdatePayload;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "읽음 상태 관리 API")
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @Operation(summary = "읽음 상태 생성", description = "특정 채널에 대한 사용자의 읽음 상태를 생성합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
      @ApiResponse(responseCode = "409", description = "이미 존재하는 상태", content = @Content)
  })
  @PostMapping
  public ResponseEntity<ReadStatusResponse> create(@RequestBody ReadStatusCreateRequest request) {
    return ResponseEntity.ok(readStatusService.create(request));
  }

  @Operation(summary = "사용자 읽음 상태 목록 조회", description = "특정 사용자의 모든 채널 읽음 상태를 조회합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  @GetMapping
  public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(@RequestParam UUID userId) {
    return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
  }

  @Operation(summary = "읽음 상태 수정", description = "마지막으로 읽은 시간을 업데이트합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "수정 성공"),
      @ApiResponse(responseCode = "404", description = "상태를 찾을 수 없음", content = @Content)
  })
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
