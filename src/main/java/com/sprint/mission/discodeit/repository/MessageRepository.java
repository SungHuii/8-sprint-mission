package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @EntityGraph(attributePaths = {"author", "author.profile", "attachments"})
  Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);

  Optional<Message> findTopByChannelIdOrderByCreatedAtDesc(UUID channelId);

  @Query("SELECT m.channel.id, MAX(m.createdAt) FROM Message m WHERE m.channel.id IN :channelIds GROUP BY m.channel.id")
  List<Object[]> findLastMessageTimes(@Param("channelIds") List<UUID> channelIds);

  void deleteAllByChannelId(UUID channelId);
}
