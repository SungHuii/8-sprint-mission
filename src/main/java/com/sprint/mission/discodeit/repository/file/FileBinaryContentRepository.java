package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.config.RepoProps;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        prefix = RepoProps.PREFIX,
        name = RepoProps.TYPE_NAME,
        havingValue = RepoProps.TYPE_FILE
)
public class FileBinaryContentRepository implements BinaryContentRepository {

    private final String filePath;
    private Map<UUID, BinaryContent> data;

    public FileBinaryContentRepository(@Value(RepoProps.FILE_DIRECTORY_PLACEHOLDER) String baseDir) {
        this.data = new HashMap<>();
        this.filePath = new File(baseDir, "binaryContentRepo.ser").getPath();
        loadFile();
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        data.put(binaryContent.getId(), binaryContent);
        saveFile();
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return ids.stream()
                .map(data::get)
                .filter(content -> content != null)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        BinaryContent removed = data.remove(id);
        if (removed == null) {
            System.out.println("해당 Binary Content가 존재하지 않습니다. id=" + id);
        }
        saveFile();
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
                data = (Map<UUID, BinaryContent>) obj;
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
