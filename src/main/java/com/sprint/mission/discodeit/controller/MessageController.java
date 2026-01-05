package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public MessageResponse create(@RequestParam UUID authorId,
                                  @RequestParam UUID channelId,
                                  @RequestParam String content,
                                  @RequestParam(required = false) List<String> attachmentData,
                                  @RequestParam(required = false) List<String> attachmentContentType,
                                  @RequestParam(required = false) List<String> attachmentOriginalName) {
        List<BinaryContentCreateRequest> attachments = toAttachmentRequests(
                attachmentData,
                attachmentContentType,
                attachmentOriginalName
        );
        MessageCreateRequest request = new MessageCreateRequest(
                authorId,
                channelId,
                content,
                attachments
        );
        return messageService.create(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<MessageResponse> findAllByChannelId(@RequestParam UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }

    @RequestMapping(value = "/{messageId}/update", method = RequestMethod.GET)
    public MessageResponse update(@PathVariable UUID messageId,
                                  @RequestParam String content) {
        MessageUpdateRequest boundRequest = new MessageUpdateRequest(
                messageId,
                content
        );
        return messageService.update(boundRequest);
    }

    @RequestMapping(value = "/{messageId}/delete", method = RequestMethod.GET)
    public void delete(@PathVariable UUID messageId) {
        messageService.deleteById(messageId);
    }

    private List<BinaryContentCreateRequest> toAttachmentRequests(List<String> dataList,
                                                                  List<String> contentTypeList,
                                                                  List<String> originalNameList) {
        if (dataList == null || dataList.isEmpty()) {
            return null;
        }
        if (contentTypeList == null || originalNameList == null) {
            throw new IllegalArgumentException("attachment metadata는 필수입니다.");
        }
        if (dataList.size() != contentTypeList.size() || dataList.size() != originalNameList.size()) {
            throw new IllegalArgumentException("attachment 파라미터 개수가 일치해야 합니다.");
        }

        List<BinaryContentCreateRequest> attachments = new ArrayList<>(dataList.size());
        for (int i = 0; i < dataList.size(); i++) {
            String data = dataList.get(i);
            if (data == null || data.isBlank()) {
                throw new IllegalArgumentException("attachment data는 필수입니다.");
            }
            byte[] decoded = Base64.getDecoder().decode(data);
            attachments.add(new BinaryContentCreateRequest(
                    decoded,
                    contentTypeList.get(i),
                    originalNameList.get(i)
            ));
        }
        return attachments;
    }
}
