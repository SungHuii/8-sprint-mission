package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.docs.ChannelApi;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @Override
  @PostMapping("/public")
  public ResponseEntity<ChannelResponse> createPublic(
      @Valid @RequestBody PublicChannelCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createPublic(request));
  }

  @Override
  @PostMapping("/private")
  public ResponseEntity<ChannelResponse> createPrivate(
      @Valid @RequestBody PrivateChannelCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createPrivate(request));
  }

  @Override
  @GetMapping
  public ResponseEntity<List<ChannelResponse>> findAll(@RequestParam UUID userId) {
    // userId 필수
    return ResponseEntity.ok(channelService.findAllByUserId(userId));
  }

  @Override
  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelResponse> update(@PathVariable UUID channelId,
      @Valid @RequestBody ChannelUpdateRequest request) {
    return ResponseEntity.ok(channelService.update(channelId, request));
  }

  @Override
  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.deleteById(channelId);
    return ResponseEntity.noContent().build();
  }
}
