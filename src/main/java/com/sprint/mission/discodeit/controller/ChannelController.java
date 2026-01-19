package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.docs.ChannelApi;
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
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @Override
  @PostMapping("/public")
  public ResponseEntity<ChannelResponse> createPublic(
      @RequestBody PublicChannelCreateRequest request) {
    return ResponseEntity.ok(channelService.createPublic(request));
  }

  @Override
  @PostMapping("/private")
  public ResponseEntity<ChannelResponse> createPrivate(
      @RequestBody PrivateChannelCreateRequest request) {
    return ResponseEntity.ok(channelService.createPrivate(request));
  }

  @Override
  @GetMapping
  public ResponseEntity<List<ChannelResponse>> findAllByUserId(@RequestParam UUID userId) {
    return ResponseEntity.ok(channelService.findAllByUserId(userId));
  }

  @Override
  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelResponse> update(@PathVariable UUID channelId,
      @RequestBody ChannelUpdateRequest request) {
    ChannelUpdateRequest newRequest = new ChannelUpdateRequest(
        request.newName(),
        request.newDescription()
    );
    return ResponseEntity.ok(channelService.update(channelId, newRequest));
  }

  @Override
  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.deleteById(channelId);
    return ResponseEntity.ok().build();
  }
}
