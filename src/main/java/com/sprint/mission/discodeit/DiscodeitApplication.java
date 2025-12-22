package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        AuthService authService = context.getBean(AuthService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        String runTag = Long.toString(System.currentTimeMillis());

        UserResponse user = setupUser(userService, runTag);
        UserResponse otherUser = setupUser2(userService, runTag);

        authLoginSuccessTest(authService, "gildong-" + runTag, "password123");
        authLoginFailTest(authService, "gildong-" + runTag, "wrong-password");

        ChannelResponse publicChannel = setupPublicChannel(channelService);
        MessageResponse publicMessage = messageCreateTest(messageService, publicChannel, user);

        ChannelResponse privateChannel = setupPrivateChannel(channelService, user, otherUser);
        MessageResponse privateMessage = messageCreateTest(messageService, privateChannel, otherUser);

        channelListByUserTest(channelService, user);
        channelUpdateTest(channelService, publicChannel);
        messageUpdateTest(messageService, publicMessage);
        userUpdateTest(userService, user);

        messageDeleteTest(messageService, privateMessage);
        channelDeleteTest(channelService, publicChannel);
    }

    static UserResponse setupUser(UserService userService, String runTag) {
        UserCreateRequest request = new UserCreateRequest(
                "홍길동",
                "gildong-" + runTag,
                "010-1234-5678",
                "password123",
                "abc+" + runTag + "@def.com",
                null
        );
        UserResponse user = userService.create(request);
        System.out.println("[유저 생성] id=" + user.id()
                + ", name=" + user.name()
                + ", nickname=" + user.nickname());
        return user;
    }

    static UserResponse setupUser2(UserService userService, String runTag) {
        UserCreateRequest request = new UserCreateRequest(
                "김철수",
                "chulsoo-" + runTag,
                "010-9876-5432",
                "password456",
                "chulsoo+" + runTag + "@def.com",
                null
        );
        UserResponse user = userService.create(request);
        System.out.println("[유저 생성] id=" + user.id()
                + ", name=" + user.name()
                + ", nickname=" + user.nickname());
        return user;
    }

    static ChannelResponse setupPublicChannel(ChannelService channelService) {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
                "일반",
                "공개 채널"
        );
        ChannelResponse channel = channelService.createPublic(request);
        System.out.println("[채널 생성] id=" + channel.id()
                + ", name=" + channel.name());
        return channel;
    }

    static ChannelResponse setupPrivateChannel(ChannelService channelService, UserResponse user, UserResponse otherUser) {
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
                List.of(user.id(), otherUser.id())
        );
        ChannelResponse channel = channelService.createPrivate(request);
        System.out.println("[채널 생성] id=" + channel.id()
                + ", type=" + channel.type()
                + ", participants=" + channel.participantIds());
        return channel;
    }

    static MessageResponse messageCreateTest(MessageService messageService, ChannelResponse channel, UserResponse user) {
        MessageCreateRequest request = new MessageCreateRequest(
                user.id(),
                channel.id(),
                "안녕하세요",
                List.of()
        );
        MessageResponse message = messageService.create(request);
        System.out.println("[메시지 생성] id=" + message.id()
                + ", message=" + message.content());

        return message;
    }

    static void channelListByUserTest(ChannelService channelService, UserResponse user) {
        List<ChannelResponse> channels = channelService.findAllByUserId(user.id());
        System.out.println("[채널 목록] userId=" + user.id() + ", size=" + channels.size());
    }

    static ChannelResponse channelUpdateTest(ChannelService channelService, ChannelResponse channel) {
        ChannelUpdateRequest request = new ChannelUpdateRequest(
                channel.id(),
                "일반-수정",
                "공개 채널 수정"
        );
        ChannelResponse updated = channelService.update(request);
        System.out.println("[채널 수정] id=" + updated.id()
                + ", name=" + updated.name());
        return updated;
    }

    static MessageResponse messageUpdateTest(MessageService messageService, MessageResponse message) {
        MessageUpdateRequest request = new MessageUpdateRequest(
                message.id(),
                "안녕하세요 - 수정"
        );
        MessageResponse updated = messageService.update(request);
        System.out.println("[메시지 수정] id=" + updated.id()
                + ", message=" + updated.content());
        return updated;
    }

    static UserResponse userUpdateTest(UserService userService, UserResponse user) {
        UserUpdateRequest request = new UserUpdateRequest(
                user.id(),
                "홍길동-수정",
                null,
                null,
                null,
                null,
                null
        );
        UserResponse updated = userService.update(request);
        System.out.println("[유저 수정] id=" + updated.id()
                + ", name=" + updated.name());
        return updated;
    }

    static void messageDeleteTest(MessageService messageService, MessageResponse message) {
        messageService.deleteById(message.id());
        System.out.println("[메시지 삭제] id=" + message.id());
    }

    static void channelDeleteTest(ChannelService channelService, ChannelResponse channel) {
        channelService.deleteById(channel.id());
        System.out.println("[채널 삭제] id=" + channel.id());
    }

    static void authLoginSuccessTest(AuthService authService, String nickname, String password) {
        LoginRequest request = new LoginRequest(nickname, password);
        var response = authService.login(request);
        System.out.println("[로그인 성공] id=" + response.id()
                + ", nickname=" + response.nickname());
    }

    static void authLoginFailTest(AuthService authService, String nickname, String password) {
        try {
            authService.login(new LoginRequest(nickname, password));
        } catch (IllegalArgumentException e) {
            System.out.println("[로그인 실패] " + e.getMessage());
        }
    }
}