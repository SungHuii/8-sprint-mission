package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    public  BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            System.out.println("이름이 비어있습니다.");
            return null;
        }
        return userRepository.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        User checkExisted = userRepository.getUser(user.getId());

        if (checkExisted == null) {
            System.out.println("해당 유저가 존재하지 않습니다.");
            return null;
        }
        checkExisted.updateName(user.getName());
        checkExisted.updateNickname(user.getNickname());
        checkExisted.updatePhoneNumber(user.getPhoneNumber());
        checkExisted.updateEmail(user.getEmail());
        checkExisted.updateAvatarUrl(user.getAvatarUrl());
        checkExisted.updatePassword(user.getPassword());

        return userRepository.updateUser(user);
    }

    @Override
    public boolean deleteUser(UUID userid) {
        return userRepository.deleteUser(userid);
    }

    @Override
    public User getUser(UUID userId) {
        return userRepository.getUser(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }
}
