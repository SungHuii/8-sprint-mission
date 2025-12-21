package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.config.RepoProps;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        prefix = RepoProps.PREFIX,
        name = RepoProps.TYPE_NAME,
        havingValue = RepoProps.TYPE_FILE
)
public class FileReadStatusRepository implements ReadStatusRepository {

    private final String filePath;
    private Map<UUID, ReadStatus> data;

    public FileReadStatusRepository(@Value(RepoProps.FILE_DIRECTORY_PLACEHOLDER) String baseDir) {
        this.data = new HashMap<>();
        this.filePath = new File(baseDir, "readStatusRepo.ser").getPath();
        loadFile();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);
        saveFile();
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        return Optional.ofNullable(data.get(readStatusId));
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        if (userId == null || channelId == null) {
            return Optional.empty();
        }
        return data.values().stream()
                .filter(status -> userId.equals(status.getUserId())
                        && channelId.equals(status.getChannelId()))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            return List.of();
        }
        return data.values().stream()
                .filter(status -> channelId.equals(status.getChannelId()))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        if (userId == null) {
            return List.of();
        }
        return data.values().stream()
                .filter(status -> userId.equals(status.getUserId()))
                .toList();
    }

    @Override
    public void deleteById(UUID readStatusId) {
        ReadStatus removed = data.remove(readStatusId);
        if (removed == null) {
            System.out.println("해당 읽음 상태가 존재하지 않습니다. id=" + readStatusId);
        }
        saveFile();
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        if (channelId == null) {
            return;
        }
        data.values().removeIf(status -> channelId.equals(status.getChannelId()));
        saveFile();
    }

    private void saveFile() {
        ensureParentDir();
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFile() {
        File file = new File(filePath);
        if (!file.exists()) return;

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(filePath))) {

            Object obj = ois.readObject();
            if (obj instanceof Map) {
                data = (Map<UUID, ReadStatus>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void ensureParentDir() {
        /* 상위 파일 디렉토리가 있는지 확인. 없으면 생성 */
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }
}
