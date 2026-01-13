package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.config.RepoProps;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
    prefix = RepoProps.PREFIX,
    name = RepoProps.TYPE_NAME,
    havingValue = RepoProps.TYPE_JCF,
    matchIfMissing = true
)
public class JCFBinaryContentRepository implements BinaryContentRepository {

  private final Map<UUID, BinaryContent> data = new ConcurrentHashMap<>();

  @Override
  public BinaryContent save(BinaryContent binaryContent) {
    data.put(binaryContent.getId(), binaryContent);
    return binaryContent;
  }

  @Override
  public Optional<BinaryContent> findById(UUID id) {
    return Optional.ofNullable(data.get(id));
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    return ids.stream()
        .map(data::get)
        .filter(content -> content != null)
        .toList();
  }

  @Override
  public void deleteById(UUID id) {
    BinaryContent removed = data.remove(id);
    if (removed == null) {
      throw new NoSuchElementException("해당 Binary Content가 존재하지 않습니다. id=" + id);
    }
  }
}
