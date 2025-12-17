package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {

    private final Map<UUID, User> data = new HashMap<>();

    @Override
    public User save(User user) {
        if (data.containsKey(user.getId())) {
            throw new IllegalStateException("이미 존재하는 유저입니다. id=" + user.getId());
        }
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!data.containsKey(user.getId())) {
            throw new NoSuchElementException("해당 유저를 찾을 수 없습니다. id=" + user.getId());
        }
        data.put(user.getId(), user);

        return user;
    }

    @Override
    public void deleteById(UUID userId) {
        User userRemoved = data.remove(userId);
        if (userRemoved == null) {
            throw new NoSuchElementException("해당 유저를 찾을 수 없습니다. id=" + userId);
        }
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(data.get(userId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();

        return data.values().stream()
                .filter(u -> email.equals(u.getEmail()))
                .findFirst();
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        if (nickname == null) return Optional.empty();

        return data.values().stream()
                .filter(u -> nickname.equals(u.getNickname()))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }
}
