package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;

  @PostMapping("/public")
  public ResponseEntity<ChannelResponse> createPublic(
      @RequestBody PublicChannelCreateRequest request) {
    return ResponseEntity.ok(channelService.createPublic(request));
  }

  @PostMapping("/private")
  public ResponseEntity<ChannelResponse> createPrivate(
      @RequestBody PrivateChannelCreateRequest request) {
    return ResponseEntity.ok(channelService.createPrivate(request));
  }

  @GetMapping
  public ResponseEntity<List<ChannelResponse>> findAllByUserId(@RequestParam UUID userId) {
    return ResponseEntity.ok(channelService.findAllByUserId(userId));
  }

  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelResponse> update(@PathVariable UUID channelId,
      @RequestBody ChannelUpdateRequest request) {
    // ChannelUpdateRequest에 channelId가 있지만, RESTful 스타일상 PathVariable을 사용하는 것이 더 나음.
    // PATCH /api/channels/123 id 값을 url 경로에 포함시키는 것이 관례
    ChannelUpdateRequest newRequest = new ChannelUpdateRequest(
        channelId,
        request.name(),
        request.description()
    );
    return ResponseEntity.ok(channelService.update(newRequest));
  }

  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.deleteById(channelId);
    return ResponseEntity.ok().build();
  }
}
