package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.docs.BinaryContentApi;
import com.sprint.mission.discodeit.dto.binary.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;
  private final UserMapper userMapper;

  @Override
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContentResponse> findById(@PathVariable UUID binaryContentId) {
    BinaryContent content = binaryContentService.findById(binaryContentId);
    return ResponseEntity.ok(userMapper.toBinaryContentResponse(content));
  }

  @Override
  @GetMapping
  public ResponseEntity<List<BinaryContentResponse>> findAllByIdIn(
      @RequestParam List<UUID> binaryContentIds) {
    List<BinaryContent> contents = binaryContentService.findAllByIdIn(binaryContentIds);
    List<BinaryContentResponse> responses = contents.stream()
        .map(userMapper::toBinaryContentResponse)
        .toList();
    return ResponseEntity.ok(responses);
  }

  @Override
  @GetMapping("/{binaryContentId}/download")
  public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
    BinaryContent content = binaryContentService.findById(binaryContentId);
    BinaryContentResponse response = userMapper.toBinaryContentResponse(content);
    return binaryContentStorage.download(response);
  }
}
