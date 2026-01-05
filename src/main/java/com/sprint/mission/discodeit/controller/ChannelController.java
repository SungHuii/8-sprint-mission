package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping(value = "/public", method = RequestMethod.GET)
    public ChannelResponse createPublic(@RequestParam String name,
                                        @RequestParam(required = false) String description) {
        return channelService.createPublic(new PublicChannelCreateRequest(name, description));
    }

    @RequestMapping(value = "/private", method = RequestMethod.GET)
    public ChannelResponse createPrivate(@RequestParam List<UUID> participantIds) {
        return channelService.createPrivate(new PrivateChannelCreateRequest(participantIds));
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelResponse> findAllByUserId(@RequestParam UUID userId) {
        return channelService.findAllByUserId(userId);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<ChannelResponse> findAll() {
        return channelService.findAll();
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
    public ChannelResponse findById(@PathVariable UUID channelId) {
        return channelService.findById(channelId);
    }

    @RequestMapping(value = "/{channelId}/update", method = RequestMethod.GET)
    public ChannelResponse update(@PathVariable UUID channelId,
                                  @RequestParam(required = false) String name,
                                  @RequestParam(required = false) String description) {
        ChannelUpdateRequest boundRequest = new ChannelUpdateRequest(
                channelId,
                name,
                description
        );
        return channelService.update(boundRequest);
    }

    @RequestMapping(value = "/{channelId}/delete", method = RequestMethod.GET)
    public void delete(@PathVariable UUID channelId) {
        channelService.deleteById(channelId);
    }
}
