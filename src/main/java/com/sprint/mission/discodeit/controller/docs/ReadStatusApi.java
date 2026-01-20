package com.sprint.mission.discodeit.controller.docs;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "읽음 상태 관리 API")
public interface ReadStatusApi {

  @Operation(summary = "읽음 상태 생성", description = "유저가 채널의 메시지를 읽었음을 기록합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
      @ApiResponse(responseCode = "404", description = "유저 또는 채널을 찾을 수 없음", content = @Content)
  })
  ResponseEntity<ReadStatusResponse> create(
      @Parameter(description = "읽음 상태 생성 요청 데이터", required = true, schema = @Schema(implementation = ReadStatusCreateRequest.class))
      @RequestBody ReadStatusCreateRequest request
  );

  @Operation(summary = "유저별 읽음 상태 목록 조회", description = "특정 유저의 모든 채널 읽음 상태를 조회합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  ResponseEntity<List<ReadStatusResponse>> findAllByUserId(
      @Parameter(description = "유저 ID", required = true) @RequestParam UUID userId
  );

  @Operation(summary = "읽음 상태 수정", description = "마지막 읽은 시간을 갱신합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "수정 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
      @ApiResponse(responseCode = "404", description = "읽음 상태를 찾을 수 없음", content = @Content)
  })
  ResponseEntity<ReadStatusResponse> update(
      @Parameter(description = "읽음 상태 ID", required = true) @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request
  );
}
