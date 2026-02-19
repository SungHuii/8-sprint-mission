package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.docs.MessageApi;
import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController implements MessageApi {

  private static final String PART_MESSAGE_CREATE = "messageCreateRequest";
  private static final String PART_ATTACHMENTS = "attachments";

  private final MessageService messageService;

  @Override
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageResponse> create(
      @Valid @RequestPart(PART_MESSAGE_CREATE) MessageCreateRequest request,
      @RequestPart(value = PART_ATTACHMENTS, required = false) List<MultipartFile> attachments
  ) throws IOException {
    List<BinaryContentCreateRequest> attachmentRequests = toAttachmentRequests(attachments);

    MessageCreateRequest newRequest = new MessageCreateRequest(
        request.content(),
        request.channelId(),
        request.authorId(),
        attachmentRequests
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(messageService.create(newRequest));
  }

  @Override
  @GetMapping
  public ResponseEntity<PageResponse<MessageResponse>> findAllByChannelId(
      @RequestParam UUID channelId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant cursor,
      @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId, cursor, pageable));
  }

  @Override
  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageResponse> update(@PathVariable UUID messageId,
      @Valid @RequestBody MessageUpdateRequest request) {
    MessageUpdateRequest newRequest = new MessageUpdateRequest(
        request.newContent()
    );
    return ResponseEntity.ok(messageService.update(messageId, newRequest));
  }

  @Override
  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    messageService.deleteById(messageId);
    return ResponseEntity.noContent().build();
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
            file.getOriginalFilename(),
            file.getSize(),
            file.getContentType(),
            file.getBytes()
        ));
      }
    }
    return requests;
  }
}
