package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileUserRepository implements UserRepository {

    private static final String FILE_PATH = "dataRepo/userRepo.ser";
    private Map<UUID, User> data;

    public FileUserRepository() {
        this.data = new HashMap<>();
        loadFile();
    }

    @Override
    public User save(User user) {
        if (data.containsKey(user.getId())) {
            throw new IllegalStateException("이미 존재하는 유저입니다. id=" + user.getId());
        }
        data.put(user.getId(), user);
        saveFile();
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!data.containsKey(user.getId())) {
            throw new NoSuchElementException("해당 유저를 찾을 수 없습니다. id=" + user.getId());
        }
        data.put(user.getId(), user);
        saveFile();
        return user;
    }

    @Override
    public void deleteById(UUID userId) {
        User userRemoved = data.remove(userId);
        if (userRemoved == null) {
            System.out.println("해당 유저를 찾을 수 없습니다.");

        }
        saveFile();
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(data.get(userId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();

        return data.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        if (nickname == null) return Optional.empty();

        return data.values().stream()
                .filter(user -> nickname.equals(user.getNickname()))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
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
                data = (Map<UUID, User>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
