package com.sprint.mission.discodeit.controller.docs;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "메시지 관리 API")
public interface MessageApi {

  @Operation(summary = "메시지 생성", description = "채널에 새로운 메시지를 생성합니다. 파일을 첨부할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
  })
  ResponseEntity<MessageResponse> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) throws IOException;

  @Operation(summary = "채널 메시지 목록 조회", description = "특정 채널의 모든 메시지를 조회합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  ResponseEntity<List<MessageResponse>> findAllByChannelId(@RequestParam UUID channelId);

  @Operation(summary = "메시지 수정", description = "메시지 내용을 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "수정 성공"),
      @ApiResponse(responseCode = "404", description = "메시지를 찾을 수 없음", content = @Content)
  })
  ResponseEntity<MessageResponse> update(@PathVariable UUID messageId, @RequestBody MessageUpdateRequest request);

  @Operation(summary = "메시지 삭제", description = "ID로 메시지를 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "삭제 성공"),
      @ApiResponse(responseCode = "404", description = "메시지를 찾을 수 없음", content = @Content)
  })
  ResponseEntity<Void> delete(@PathVariable UUID messageId);
}
