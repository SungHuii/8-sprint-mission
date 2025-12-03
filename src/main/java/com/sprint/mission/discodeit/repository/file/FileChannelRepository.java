package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {

    private static final String FILE_PATH = "channelRepo.ser";
    private Map<UUID, Channel> data;

    public FileChannelRepository() {
        this.data = new HashMap<>();
        load();
    }

    private void save() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_PATH))) {

            Object obj = ois.readObject();
            if (obj instanceof Map) {
                data = (Map<UUID, Channel>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Channel createChannel(Channel channel) {
        if (data.containsKey(channel.getId())) {
            System.out.println("이미 존재하는 채널입니다.");
            return null;
        }
        data.put(channel.getId(), channel);
        save();
        return channel;
    }

    @Override
    public Channel updateChannel(Channel channel) {
        Channel existingChannel = data.get(channel.getId());
        if (existingChannel != null) {
            existingChannel.updateChName(channel.getChName());
            existingChannel.updateChDescription(channel.getChDescription());
            save();
        } else {
            System.out.println("해당 채널을 찾을 수 없습니다.");
            return null;
        }
        return existingChannel;
    }

    @Override
    public boolean deleteChannel(UUID channelId) {
        Channel channelRemoved = data.remove(channelId);
        save();
        if (channelRemoved != null ) {
            System.out.println("채널이 성공적으로 삭제되었습니다.");
            return true;
        } else {
            System.out.println("해당 채널을 찾을 수 없습니다.");
            return false;
        }
    }

    @Override
    public Channel getChannel(UUID channelId) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            System.out.println("해당 채널을 찾을 수 없습니다.");
            return null;
        }
        return channel;
    }

    @Override
    public List<Channel> getAllChannels() {
        List<Channel> allChannels = new ArrayList<>(data.values());
        return allChannels;
    }
}
