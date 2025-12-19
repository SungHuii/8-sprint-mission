package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(BinaryContentCreateRequest request) {
        validateCreateRequest(request);

        BinaryContent content = new BinaryContent(
                request.data(),
                request.contentType(),
                request.originalName()
        );

        return binaryContentRepository.save(content);
    }

    @Override
    public BinaryContent findById(UUID binaryContentId) {
        if (binaryContentId == null) {
            throw new IllegalArgumentException("binaryContentId는 필수입니다.");
        }

        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 BinaryContent가 존재하지 않습니다. binaryContentId=" + binaryContentId));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ids는 필수이며 비어 있을 수 없습니다.");
        }

        return binaryContentRepository.findAllByIdIn(ids);
    }

    @Override
    public void deleteById(UUID binaryContentId) {
        if (binaryContentId == null) {
            throw new IllegalArgumentException("binaryContentId는 필수입니다.");
        }

        binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 BinaryContent가 존재하지 않습니다. binaryContentId=" + binaryContentId));

        binaryContentRepository.deleteById(binaryContentId);
    }

    private void validateCreateRequest(BinaryContentCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("요청이 null입니다.");
        }
        if (request.data() == null || request.data().length == 0) {
            throw new IllegalArgumentException("data는 필수입니다.");
        }
        if (request.contentType() == null || request.contentType().isBlank()) {
            throw new IllegalArgumentException("contentType은 필수입니다.");
        }
        if (request.originalName() == null || request.originalName().isBlank()) {
            throw new IllegalArgumentException("originalName은 필수입니다.");
        }
    }
}

