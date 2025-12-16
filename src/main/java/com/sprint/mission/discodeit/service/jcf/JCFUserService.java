package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    /* JCF(Java Collections Framework) 기반으로 데이터를 저장할 수 있는 필드(data)를 final로 선언하고 생성자에서 초기화
    * data 필드를 활용해서 CRUD 메소드 구현
    */
    private final Map<UUID, User> data;
    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        if (data.containsKey(user.getId())) {
            System.out.println("이미 존재하는 유저입니다.");
            return null;
        }
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User saveUser(String name, String nickname, String phoneNumber, String password, String email) {
        return new User(name, nickname, phoneNumber, password, email);
    }

    @Override
    public User updateUser(User user) {
        User existingUser = data.get(user.getId());
        if (existingUser == null) {
            System.out.println("해당 유저를 찾을 수 없습니다.");
            return null;
        }
        existingUser.updateName(user.getName());
        existingUser.updateNickname(user.getNickname());
        existingUser.updatePhoneNumber(user.getPhoneNumber());
        existingUser.updateEmail(user.getEmail());
        existingUser.updateProfileId(user.getProfileId());
        existingUser.updatePassword(user.getPassword());
        System.out.println("유저 정보가 성공적으로 업데이트되었습니다.");

        return existingUser;
    }

    @Override
    public boolean deleteUser(UUID userId) {
        User userRemoved = data.remove(userId);
        if (userRemoved == null) {
            System.out.println("해당 유저를 찾을 수 없습니다.");
            return false;
        }
        System.out.println("유저가 성공적으로 삭제되었습니다.");
        return true;
    }

    @Override
    public User findById(UUID userId) {
        User user = data.get(userId);
        if (user == null) {
            System.out.println("해당 유저를 찾을 수 없습니다.");
            return null;
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }
}
