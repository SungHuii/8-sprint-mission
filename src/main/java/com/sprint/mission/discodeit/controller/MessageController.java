package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "메시지 관리 API")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

  private static final String PART_MESSAGE_CREATE = "messageCreateRequest";
  private static final String PART_ATTACHMENTS = "attachments";

  private final MessageService messageService;

  @Operation(summary = "메시지 생성", description = "채널에 새로운 메시지를 생성합니다. 파일을 첨부할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageResponse> create(
      @RequestPart(PART_MESSAGE_CREATE) MessageCreateRequest request,
      @RequestPart(value = PART_ATTACHMENTS, required = false) List<MultipartFile> attachments
  ) throws IOException {
    List<BinaryContentCreateRequest> attachmentRequests = toAttachmentRequests(attachments);

    MessageCreateRequest newRequest = new MessageCreateRequest(
        request.authorId(),
        request.channelId(),
        request.content(),
        attachmentRequests
    );

    return ResponseEntity.ok(messageService.create(newRequest));
  }

  @Operation(summary = "채널 메시지 목록 조회", description = "특정 채널의 모든 메시지를 조회합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  @GetMapping
  public ResponseEntity<List<MessageResponse>> findAllByChannelId(@RequestParam UUID channelId) {
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
  }

  @Operation(summary = "메시지 수정", description = "메시지 내용을 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "수정 성공"),
      @ApiResponse(responseCode = "404", description = "메시지를 찾을 수 없음", content = @Content)
  })
  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageResponse> update(@PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    MessageUpdateRequest newRequest = new MessageUpdateRequest(
        messageId,
        request.content()
    );
    return ResponseEntity.ok(messageService.update(newRequest));
  }

  @Operation(summary = "메시지 삭제", description = "ID로 메시지를 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "삭제 성공"),
      @ApiResponse(responseCode = "404", description = "메시지를 찾을 수 없음", content = @Content)
  })
  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    messageService.deleteById(messageId);
    return ResponseEntity.ok().build();
  }

  private List<BinaryContentCreateRequest> toAttachmentRequests(List<MultipartFile> files)
      throws IOException {
    if (files == null || files.isEmpty()) {
      return null;
    }
    List<BinaryContentCreateRequest> requests = new ArrayList<>(files.size());
    for (MultipartFile file : files) {
      if (!file.isEmpty()) {
        requests.add(new BinaryContentCreateRequest(
            file.getBytes(),
            file.getContentType(),
            file.getOriginalFilename()
        ));
      }
    }
    return requests;
  }
}
