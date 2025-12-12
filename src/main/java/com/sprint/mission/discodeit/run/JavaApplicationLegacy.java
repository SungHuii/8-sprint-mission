package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class JavaApplicationLegacy {
    public static void main(String[] args) {

        // 서비스 구현체 생성
        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService();

        // 의존성 주입
        UserService jcfUserService = new JCFUserService();
        ChannelService jcfChannelService = new JCFChannelService();
        MessageService jcfMessageService = new JCFMessageService(jcfUserService, jcfChannelService);

        /* 도메인 별 서비스 구현체 테스트 */
        /* User */
        System.out.println("============ User ===========");
        // 등록
        List<User> users = Arrays.asList(
                new User("홍길동", "gildong", "010-1234-5678", "password123", "abc@def.com", "gildong.jpg"),
                new User("김철수", "chulsoo", "010-8765-4321", "password456", "chulsoo@chul.com", "chulsoo.jpg"),
                new User("이영수", "youngsoo", "010-1111-2222", "password789", "youngsoo@naver.com", "youngsoo.jpg")
        );
        users.forEach(userService::save);

        // 이름으로 사용자명 조회
        System.out.println("---- 이름에 '수'가 포함된 유저 조회 ----");
        List<User> filteredUser = userService.findAll()
                .stream()
                        .filter(u -> u.getName().contains("수"))
                                .toList();
        filteredUser.forEach(user -> System.out.println("이름에 '수'가 포함된 유저: " + user.getName()));

        // 전체 조회
        System.out.println("---- 전체 유저 목록 조회 ----");
        userService.findAll().forEach( u -> {
            System.out.println("- " + u.getName() + " (" + u.getNickname() + ")");
        });

        // 수정
        System.out.println("--- 홍길동 닉네임 / 이메일 수정 ---");
        User gildong = users.get(0);
        System.out.println("수정 전 닉네임 : " + gildong.getNickname());
        System.out.println("수정 전 이메일 : " + gildong.getEmail());
        System.out.println("수정 전 updatedAt : " + formatTime(userService.findById(gildong.getId()).getUpdatedAt()) + "");
        gildong.updateNickname("new-gildong");
        gildong.updateEmail("new-gildong@dong.com");
        userService.updateUser(gildong);
        // 수정된 데이터 조회
        System.out.println("수정 후 닉네임 : " + gildong.getNickname());
        System.out.println("수정 후 이메일 : " + gildong.getEmail());
        System.out.println("수정 후 updatedAt : " + formatTime(userService.findById(gildong.getId()).getUpdatedAt()) + "");
        //System.out.println("수정된 닉네임 : " + userService.getUser(gildong.getId()).getNickname());
        //System.out.println("수정된 이메일 : " + userService.getUser(gildong.getId()).getEmail());

        // 삭제
        System.out.println("--- 김철수 유저 삭제 ---");
        User chulsoo = users.get(1);
        userService.deleteUser(chulsoo.getId());

        // 삭제 확인
        // deleteUser에 삭제 확인 출력문을 넣어놓았음.
        User deletedChulsoo = userService.findById(chulsoo.getId());
        if (deletedChulsoo == null) {
            System.out.println("김철수 유저는 삭제 되었습니다.");
        } else {
            System.out.println("삭제 실패! 김철수 유저가 유저 목록에 남아 있습니다!");
        }

        /* Channel */
        System.out.println("============ Channel ===========");
        // 등록
        List<Channel> channels = Arrays.asList(
                new Channel("일반", "일반 채널입니다."),
                new Channel("공지사항", "공지사항 채널입니다."),
                new Channel("자유게시판", "자유롭게 이야기하는 채널입니다.")
        );
        channels.forEach(channelService::save);

        // 채널명으로 조회
        System.out.println("---- 이름에 '공지'가 포함된 채널 조회 ----");
        List<Channel> filteredChannel = channelService.findAll()
                .stream()
                .filter(c -> c.getChName().contains("공지"))
                .toList();
        filteredChannel.forEach(channel -> System.out.println("이름에 '공지'가 포함된 채널: " + channel.getChName()));

        // 전체 조회
        System.out.println("---- 전체 채널 목록 조회 ----");
        channelService.findAll().forEach(c -> {
            System.out.println("- " + c.getChName() + " (" + c.getChDescription() + ")");
        });

        // 수정
        System.out.println("--- '일반' 채널명 / 채널설명 수정 ---");
        Channel normalCh = channels.get(0);
        System.out.println("수정 전 채널명 : " + normalCh.getChName());
        System.out.println("수정 전 채널설명 : " + normalCh.getChDescription());
        System.out.println("수정 전 updatedAt : " + formatTime(channelService.findById(normalCh.getId()).getUpdatedAt()) + "");
        normalCh.updateChName("수정된 일반 채널명");
        normalCh.updateChDescription("수정된 일반 채널 설명");
        channelService.updateChannel(normalCh);
        // 수정된 데이터 조회
        System.out.println("수정 후 채널명 : " + normalCh.getChName());
        System.out.println("수정 후 채널설명 : " + normalCh.getChDescription());
        System.out.println("수정 후 updatedAt : " + formatTime(channelService.findById(normalCh.getId()).getUpdatedAt()) + "");

        // 삭제
        System.out.println("--- 공지사항 채널 삭제 ---");
        Channel noticeCh = channels.get(1);
        channelService.deleteChannel(noticeCh.getId());

        // 삭제 확인
        Channel deletedNoticeCh = channelService.findById(noticeCh.getId());
        if (deletedNoticeCh == null) {
            System.out.println("공지사항 채널은 삭제 되었습니다.");
        } else {
            System.out.println("삭제 실패! 공지사항 채널이 채널 목록에 남아 있습니다!");
        }

        /* Message */
        System.out.println("============ Message ===========");
        // 등록
        List<Message> messages = Arrays.asList(
                new Message(users.get(0).getId(), channels.get(0).getId(), "안녕하세요, 관리자 홍길동입니다!"),
                new Message(users.get(2).getId(), channels.get(0).getId(), "하이요, 이영수입니다"),
                new Message(users.get(0).getId(), channels.get(2).getId(), "자유게시판에 오신 것을 환영합니다!")
        );
        messages.forEach(messageService::save);

        // 메시지 내용으로 조회
        System.out.println("---- 이름에 '관리'가 포함된 메시지 조회 ----");
        List<Message> filteredMessage = messageService.findAll()
                .stream()
                .filter(m -> m.getMessage().contains("관리"))
                .toList();
        filteredMessage.forEach(m -> System.out.println("이름에 '관리'가 포함된 메시지: " + m.getMessage()));

        // 전체 조회 (메시지 내용 + 생성시각)
        System.out.println("---- 전체 메시지 목록 조회 ----");
        messageService.findAll().forEach( m -> {
            System.out.println("- " + m.getMessage() + " (" + formatTime(m.getCreatedAt()) + ")");
        });

        // 수정
        System.out.println("--- 첫번째 메시지 수정 ---");
        Message firstMsg = messages.get(0);
        System.out.println("수정 전 메시지 : " + firstMsg.getMessage());
        System.out.println("수정 전 updatedAt : " + formatTime(messageService.findById(firstMsg.getId()).getUpdatedAt()) + "");
        firstMsg.updateMessage("하이요, 관리자 홍길동입니다!");
        messageService.updateMessage(firstMsg);
        // 수정된 데이터 조회
        System.out.println("수정 후 메시지 : " + firstMsg.getMessage());
        System.out.println("수정 후 updatedAt : " + formatTime(messageService.findById(firstMsg.getId()).getUpdatedAt()) + "");

        // 삭제
        System.out.println("--- 이영수 메시지 삭제 ---");
        Message ysMsg = messages.get(1);
        messageService.deleteMessage(ysMsg.getId());

        // 삭제 확인
        Message deletedYsMsg = messageService.findById(ysMsg.getId());
        if (deletedYsMsg == null) {
            System.out.println("메시지가 삭제되었습니다.");
        } else {
            System.out.println("삭제 실패! 메시지가 아직 남아있습니다.");
        }
    }

    /* System.currentTimeMillis() 형식을 예쁘게 볼 수 있도록 변경
    * 초 단위까지만 나타내면 차이가 보이지 않아, 밀리초까지 포함 */
    private static String formatTime(Long epochMillis) {
        return Instant.ofEpochMilli(epochMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }
}
