package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.config.RepoProps;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = RepoProps.PREFIX,
        name = RepoProps.TYPE_NAME,
        havingValue = RepoProps.TYPE_FILE
)
public class FileChannelRepository implements ChannelRepository {

    private final String filePath;
    private Map<UUID, Channel> data;

    public FileChannelRepository(@Value(RepoProps.FILE_DIRECTORY_PLACEHOLDER) String baseDir) {
        this.data = new HashMap<>();
        this.filePath = new File(baseDir, "channelRepo.ser").getPath();
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
    public Channel updateChannel(Channel channel) {
        Channel existingChannel = data.get(channel.getId());
        if (existingChannel == null) {
            System.out.println("해당 채널을 찾을 수 없습니다.");
            return null;
        }
        existingChannel.updateChName(channel.getChName());
        existingChannel.updateChDescription(channel.getChDescription());
        saveFile();

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
    public Optional<Channel> findById(UUID channelId) {
        return Optional.ofNullable(data.get(channelId));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    private void saveFile() {
        ensureParentDir();
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFile() {
        File file = new File(filePath);
        if (!file.exists()) return;

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(filePath))) {

            Object obj = ois.readObject();
            if (obj instanceof Map) {
                data = (Map<UUID, Channel>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void ensureParentDir() {
        /* 상위 파일 디렉토리가 있는지 확인. 없으면 생성 */
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }
}
