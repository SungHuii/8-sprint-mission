package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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
public class FileReadStatusRepository implements ReadStatusRepository {

    private static final String FILE_PATH = "dataRepo/readStatusRepo.ser";
    private Map<UUID, ReadStatus> data;

    public FileReadStatusRepository() {
        this.data = new HashMap<>();
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
            System.out.println("해당 ReadStatus가 존재하지 않습니다. id=" + readStatusId);
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
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_PATH))) {

            Object obj = ois.readObject();
            if (obj instanceof Map) {
                data = (Map<UUID, ReadStatus>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
