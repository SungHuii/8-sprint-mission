package com.sprint.mission.discodeit.run;
/*

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public class JavaApplicationBasic {
    public static void main(String[] args) {

        */
/* Basic + JCF 테스트 *//*

        // Repository 주입
        UserRepository userRepo = new JCFUserRepository();
        ChannelRepository channelRepo = new JCFChannelRepository();
        MessageRepository messageRepo = new JCFMessageRepository();

        // Basic Service
        UserService userServ = new BasicUserService(userRepo);
        ChannelService channelServ = new BasicChannelService(channelRepo);
        MessageService messageServ = new BasicMessageService(messageRepo, userRepo, channelRepo);

        // 테스트
        User user = setupUser(userServ);
        Channel channel = setupChannel(channelServ);
        messageCreateTest(messageServ, channel, user);

        */
/* Basic + File 테스트 *//*

        UserRepository fileUserRepo = new FileUserRepository();
        ChannelRepository fileChannelRepo = new FileChannelRepository();
        MessageRepository fileMessageRepo = new FileMessageRepository();

        UserService userServ2 = new BasicUserService(fileUserRepo);
        ChannelService channelServ2 = new BasicChannelService(fileChannelRepo);
        MessageService messageServ2 = new BasicMessageService(
                fileMessageRepo, fileUserRepo, fileChannelRepo);

        User user2 = setupUser(userServ2);
        Channel channel2 = setupChannel(channelServ2);
        messageCreateTest(messageServ2, channel2, user2);

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
        Message message = messageService.saveMessage(user.getId(), channel.getId(), "안녕하세요, 반갑습니다!", null);
        if (message == null) {
            System.out.println("[Message 생성 실패]");
        }
        System.out.println("[Message 생성] id=" + message.getId()
                + ", message=" + message.getMessageContent());

        return message;
    }
}
*/
