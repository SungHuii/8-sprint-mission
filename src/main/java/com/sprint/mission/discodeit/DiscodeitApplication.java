package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        messageCreateTest(messageService, channel, user);

    }

    static User setupUser(UserService userService) {
        User user = userService.saveUser("홍길동", "gildong", "010-1234-5678", "password123", "abc@def.com");
        System.out.println("[User 생성] id=" + user.getId()
                + ", name=" + user.getName()
                + ", nickname=" + user.getNickname());
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.saveChannel("공지", "공지 채널입니다");
        System.out.println("[Channel 생성] id=" + channel.getId()
                + ", name=" + channel.getChName());
        return channel;
    }

    static Message messageCreateTest(MessageService messageService, Channel channel, User user) {
        Message message = messageService.saveMessage(user.getId(), channel.getId(), "안녕하세요, 반갑습니다!");
        if (message == null) {
            System.out.println("[Message 생성 실패]");
            return null;
        }
        System.out.println("[Message 생성] id=" + message.getId()
                + ", message=" + message.getMessage());

        return message;
    }

}
