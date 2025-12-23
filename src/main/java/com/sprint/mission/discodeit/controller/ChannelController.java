package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
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
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ChannelResponse createPublic(@RequestBody PublicChannelCreateRequest request) {
        return channelService.createPublic(request);
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ChannelResponse createPrivate(@RequestBody PrivateChannelCreateRequest request) {
        return channelService.createPrivate(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelResponse> findAllByUserId(@RequestParam UUID userId) {
        return channelService.findAllByUserId(userId);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
    public ChannelResponse findById(@PathVariable UUID channelId) {
        return channelService.findById(channelId);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.PUT)
    public ChannelResponse update(@PathVariable UUID channelId, @RequestBody ChannelUpdateRequest request) {
        ChannelUpdateRequest boundRequest = new ChannelUpdateRequest(
                channelId,
                request.name(),
                request.description()
        );
        return channelService.update(boundRequest);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID channelId) {
        channelService.deleteById(channelId);
    }
}
