package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Notification;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  // 알림 최신순 조회
  List<Notification> findAllByReceiverIdOrderByCreatedAtDesc(UUID receiverId);
}
