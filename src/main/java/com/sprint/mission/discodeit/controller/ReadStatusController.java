package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/read-statuses")
@RequiredArgsConstructor
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @RequestMapping(value = "/create", method = RequestMethod.GET)
  public ReadStatusResponse create(@RequestParam UUID userId,
      @RequestParam UUID channelId,
      @RequestParam(required = false) String lastReadAt) {
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(
        userId,
        channelId,
        parseInstant(lastReadAt)
    );
    return readStatusService.create(request);
  }

  @RequestMapping(method = RequestMethod.GET)
  public List<ReadStatusResponse> findAllByUserId(@RequestParam UUID userId) {
    return readStatusService.findAllByUserId(userId);
  }

  @RequestMapping(value = "/{readStatusId}/update", method = RequestMethod.GET)
  public ReadStatusResponse update(@PathVariable UUID readStatusId,
      @RequestParam(required = false) String lastReadAt) {
    ReadStatusUpdateRequest boundRequest = new ReadStatusUpdateRequest(
        readStatusId,
        parseInstant(lastReadAt)
    );
    return readStatusService.update(boundRequest);
  }

  @RequestMapping(value = "/{readStatusId}/delete", method = RequestMethod.GET)
  public void delete(@PathVariable UUID readStatusId) {
    readStatusService.deleteById(readStatusId);
  }

  private Instant parseInstant(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return Instant.parse(value);
  }
}
