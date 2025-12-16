package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            System.out.println("이름이 비어있습니다.");
            return null;
        }
        return userRepository.save(user);
    }

    @Override
    public User saveUser(String name, String nickname, String phoneNumber, String password, String email) {
        User user = new User(name, nickname, phoneNumber, password, email);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        User checkExisted = userRepository.findById(user.getId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        checkExisted.updateName(user.getName());
        checkExisted.updateNickname(user.getNickname());
        checkExisted.updatePhoneNumber(user.getPhoneNumber());
        checkExisted.updateEmail(user.getEmail());
        checkExisted.updateProfileId(user.getProfileId());
        checkExisted.updatePassword(user.getPassword());

        return userRepository.updateUser(checkExisted);
    }

    @Override
    public void deleteById(UUID userid) {
        userRepository.deleteById(userid);
    }

    @Override
    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
