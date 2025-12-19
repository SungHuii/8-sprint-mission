package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.enums.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
        UUID id,
        ChannelType type,
        String name,                // PUBLIC 梨꾨꼸??寃쎌슦留?媛믪씠 ?덉쓬
        String description,         // PUBLIC 梨꾨꼸??寃쎌슦留?媛믪씠 ?덉쓬
        Instant lastMessageAt,      // 硫붿떆吏媛 ?녿떎硫?null
        List<UUID> participantIds   // PUBLIC 梨꾨꼸??寃쎌슦 鍮?由ъ뒪??/ PRIVATE 梨꾨꼸??寃쎌슦 李몄뿬??紐⑸줉
) {
}

