package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {

    /*
    * 직렬화UID
    * 고유아이디
     * 생성시간
     * 수정시간
     * 별명(닉네임)
     * 전화번호
     * 패스워드
     * 이메일정보
     * 사진(아바타)
    * */
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private String name;
    private String nickname;
    private String phoneNumber;
    private transient String password;
    private String email;
    private String avatarUrl;

    public User(String name, String nickname, String phoneNumber, String password, String email, String avatarUrl) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;

        this.name = name;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public void updateName(String name) {
        this.name = name;
        renewUpdatedAt();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
        renewUpdatedAt();
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        renewUpdatedAt();
    }

    public void updatePassword(String password) {
        this.password = password;
        renewUpdatedAt();
    }

    public void updateEmail(String email) {
        this.email = email;
        renewUpdatedAt();
    }

    public void updateAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        renewUpdatedAt();
    }

    private void renewUpdatedAt() {
        this.updatedAt = Instant.now();
    }
}
