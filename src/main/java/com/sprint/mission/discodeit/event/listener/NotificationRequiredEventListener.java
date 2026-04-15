package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final ReadStatusRepository readStatusRepository;
  private final NotificationService notificationService;

  // 새 메시지 등록 시
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) {

    List<ReadStatus> activeStatuses = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(
        event.channelId());

    for (ReadStatus status : activeStatuses) {
      if (status.getUser().getId().equals(event.authorId())) {
        continue;
      }

      String title = "새로운 메시지 (" + event.channelName() + ")";
      notificationService.create(status.getUser().getId(), title, event.content());
    }

    log.info("채널 메시지 알림 생성 완료. channelId : {}", event.channelId());
  }

  // 권한 변경 시
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) {

    String title = "권한이 변경되었습니다.";
    String content = event.oldRole() + " -> " + event.newRole();

    notificationService.create(event.userId(), title, content);

    log.info("권한 변경 알림 생성 완료. userId : {}", event.userId());
  }
}
