package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.enums.Role;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;
  private final NotificationService notificationService;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;

  // 메시지 알림 처리
  @KafkaListener(topics = "discodeit.MessageCreatedEvent", groupId = "discodeit-group")
  public void onMessageCreatedEvent(String kafkaEvent) {

    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

      // 기존 리스너 로직
      List<ReadStatus> activeStatuses = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(
          event.channelId());
      for (ReadStatus readStatus : activeStatuses) {
        if (readStatus.getUser().getId().equals(event.authorId())) {
          continue;
        }

        String title = event.authorName() + " (#" + event.channelName() + ")";
        notificationService.create(readStatus.getUser().getId(), title, event.content());
      }
    } catch (JsonProcessingException e) {
      log.error("메시지 역직렬화 실패: {}", e.getMessage());
    }
  }

  // 권한 변경 알림 처리
  @KafkaListener(topics = "discodeit.RoleUpdatedEvent", groupId = "discodeit-group")
  public void onRoleUpdatedEvent(String kafkaEvent) {

    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

      String title = "권한이 변경되었습니다.";
      String content = event.oldRole() + " -> " + event.newRole();
      notificationService.create(event.userId(), title, content);
    } catch (JsonProcessingException e) {
      log.error("권한 변경 이벤트 역직렬화 실패: {}", e.getMessage());
    }
  }

  // S3 업로드 실패 관리자 알림 처리
  @KafkaListener(topics = "discodeit.S3UploadFailedEvent", groupId = "discodeit-group")
  public void onS3UploadFailedEvent(String kafkaEvent) {

    try {
      S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
      // 모든 관리자(ADMIN)에게 알림
      List<User> admins = userRepository.findAllByRole(Role.ADMIN);

      String title = "[시스템 에러] 파일 업로드 최종 실패";
      String content = String.format("RequestId: %s\nContentId: %s\nError: %s",
          event.requestId(), event.binaryContentId(), event.errorMessage());

      for (User admin : admins) {
        notificationService.create(admin.getId(), title, content);
      }
      log.warn("관리자 알림 생성 완료: {}명", admins.size());
    } catch (JsonProcessingException e) {
      log.error("실패 이벤트 역직렬화 실패");
    }
  }
}
