package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/read-statuses")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ReadStatusResponse create(@RequestBody ReadStatusCreateRequest request) {
        return readStatusService.create(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatusResponse> findAllByUserId(@RequestParam UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }

    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PUT)
    public ReadStatusResponse update(@PathVariable UUID readStatusId,
                                     @RequestBody ReadStatusUpdateRequest request) {
        ReadStatusUpdateRequest boundRequest = new ReadStatusUpdateRequest(
                readStatusId,
                request.lastReadAt()
        );
        return readStatusService.update(boundRequest);
    }

    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID readStatusId) {
        readStatusService.deleteById(readStatusId);
    }
}
