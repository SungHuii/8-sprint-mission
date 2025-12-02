package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

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
    private final Long createdAt;
    private Long updatedAt;
    private String name;
    private String nickname;
    private String phoneNumber;
    private String password;
    private String email;
    private String avatarUrl;

    public User(String name, String nickname, String phoneNumber, String password, String email, String avatarUrl) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;

        this.name = name;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void updateName(String name) {
        this.name = name;
        renewUpdatedAt();
    }

    public String getNickname() {
        return nickname;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
        renewUpdatedAt();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        renewUpdatedAt();
    }

    public String getPassword() {
        return password;
    }

    public void updatePassword(String password) {
        this.password = password;
        renewUpdatedAt();
    }

    public String getEmail() {
        return email;
    }

    public void updateEmail(String email) {
        this.email = email;
        renewUpdatedAt();
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void updateAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        renewUpdatedAt();
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    private void renewUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
    }
}
