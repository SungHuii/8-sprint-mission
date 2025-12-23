package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
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
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public MessageResponse create(@RequestBody MessageCreateRequest request) {
        return messageService.create(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<MessageResponse> findAllByChannelId(@RequestParam UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.PUT)
    public MessageResponse update(@PathVariable UUID messageId, @RequestBody MessageUpdateRequest request) {
        MessageUpdateRequest boundRequest = new MessageUpdateRequest(
                messageId,
                request.content()
        );
        return messageService.update(boundRequest);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID messageId) {
        messageService.deleteById(messageId);
    }
}
