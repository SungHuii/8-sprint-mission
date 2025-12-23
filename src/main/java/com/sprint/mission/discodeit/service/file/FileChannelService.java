package com.sprint.mission.discodeit.service.file;
/*

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {

    */
/*
     * File IO를 통한 데이터 영속화
     * FileIO와 객체 직렬화를 활용해 메소드 구현
     * *//*

    private static final String FILE_PATH = "data/channel.ser";
    private Map<UUID, Channel> data;

    public FileChannelService() {
        this.data = new HashMap<>();
        loadFile();
    }


    @Override
    public Channel save(Channel channel) {
        if (data.containsKey(channel.getId())) {
            System.out.println("이미 존재하는 채널입니다.");
            return null;
        }
        data.put(channel.getId(), channel);
        saveFile();
        return channel;
    }

    @Override
    public Channel saveChannel(String name, String description) {
        return new Channel(name, description);
    }

    @Override
    public Channel updateChannel(Channel channel) {
        Channel existingChannel = data.get(channel.getId());
        if (existingChannel == null) {
            System.out.println("해당 채널을 찾을 수 없습니다.");
            return null;
        }
        existingChannel.updateChName(channel.getChName());
        existingChannel.updateChDescription(channel.getChDescription());
        saveFile();
        System.out.println("채널이 성공적으로 업데이트되었습니다.");

        return existingChannel;
    }

    @Override
    public boolean deleteChannel(UUID channelId) {
        Channel channelRemoved = data.remove(channelId);
        saveFile();
        if (channelRemoved == null ) {
            System.out.println("해당 채널을 찾을 수 없습니다.");
            return false;
        }
        System.out.println("채널이 성공적으로 삭제되었습니다.");
        return true;
    }

    @Override
    public Channel findById(UUID channelId) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            System.out.println("해당 채널을 찾을 수 없습니다.");
            return null;
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    */
/* 파일 저장 *//*

    private void saveFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    */
/* 파일 불러오기 *//*

    private void loadFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return;   // 파일 없으면 skip
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                this.data = (Map<UUID, Channel>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
*/
