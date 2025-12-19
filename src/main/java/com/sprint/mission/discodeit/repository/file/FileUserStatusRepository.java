package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
public class FileUserStatusRepository implements UserStatusRepository {

    private static final String FILE_PATH = "dataRepo/userStatusRepo.ser";
    private Map<UUID, UserStatus> data;

    public FileUserStatusRepository() {
        this.data = new HashMap<>();
        loadFile();
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        data.put(userStatus.getId(), userStatus);
        saveFile();
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID userStatusId) {
        return Optional.ofNullable(data.get(userStatusId));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return data.values().stream()
                .filter(status -> userId.equals(status.getUserId()))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public void deleteById(UUID userStatusId) {
        UserStatus removed = data.remove(userStatusId);
        if (removed == null) {
            System.out.println("해당 유저 상태가 존재하지 않습니다. id=" + userStatusId);
        }
        saveFile();
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Optional<UserStatus> existing = findByUserId(userId);
        if (existing.isEmpty()) {
            System.out.println("해당 유저 상태가 존재하지 않습니다. userId=" + userId);
            return;
        }
        data.remove(existing.get().getId());
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
                data = (Map<UUID, UserStatus>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}