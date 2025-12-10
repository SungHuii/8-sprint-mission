package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {

    /*
     * File IO를 통한 데이터 영속화
     * FileIO와 객체 직렬화를 활용해 메소드 구현
     * */
    private static final String FILE_PATH = "data/user.ser";
    private Map<UUID, User> data;

    public FileUserService() {
        this.data = new HashMap<>();
        load();
    }

    /* 파일 저장 */
    private void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 파일 불러오기 */
    private void load() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_PATH))) {

            Object obj = ois.readObject();
            if (obj instanceof Map) {
                this.data = (Map<UUID, User>) obj;
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User createUser(User user) {
        data.put(user.getId(), user);
        save();
        return user;
    }

    @Override
    public User updateUser(User user) {
        User existingUser = data.get(user.getId());
        if (existingUser != null) {
            existingUser.updateName(user.getName());
            existingUser.updateNickname(user.getNickname());
            existingUser.updatePhoneNumber(user.getPhoneNumber());
            existingUser.updateEmail(user.getEmail());
            existingUser.updateAvatarUrl(user.getAvatarUrl());
            existingUser.updatePassword(user.getPassword());
            save();
        } else {
            System.out.println("해당 유저를 찾을 수 없습니다.");
            return null;
        }
        return existingUser;
    }

    @Override
    public boolean deleteUser(UUID userId) {
        User userRemoved = data.remove(userId);
        if (userRemoved != null) {
            System.out.println("유저가 성공적으로 삭제되었습니다.");
            save();
            return true;
        } else {
            System.out.println("해당 유저를 찾을 수 없습니다.");
            return false;
        }
    }

    @Override
    public User getUser(UUID userId) {
        User user = data.get(userId);
        if (user == null) {
            System.out.println("해당 유저를 찾을 수 없습니다.");
            return null;
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> allUsers =  new ArrayList<>(data.values());
        return allUsers;
    }
}
