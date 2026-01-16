package com.sprint.mission.discodeit.controller.docs;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "채널 관리 API")
public interface ChannelApi {

  @Operation(summary = "공개 채널 생성", description = "누구나 참여할 수 있는 공개 채널을 생성합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
  })
  ResponseEntity<ChannelResponse> createPublic(@RequestBody PublicChannelCreateRequest request);

  @Operation(summary = "비공개 채널 생성", description = "초대된 사용자만 참여할 수 있는 비공개 채널(DM)을 생성합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
  })
  ResponseEntity<ChannelResponse> createPrivate(@RequestBody PrivateChannelCreateRequest request);

  @Operation(summary = "사용자 채널 목록 조회", description = "특정 사용자가 참여하고 있는 모든 채널 목록을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  ResponseEntity<List<ChannelResponse>> findAllByUserId(@RequestParam UUID userId);

  @Operation(summary = "채널 정보 수정", description = "채널의 이름이나 설명을 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "수정 성공"),
      @ApiResponse(responseCode = "404", description = "채널을 찾을 수 없음", content = @Content)
  })
  ResponseEntity<ChannelResponse> update(@PathVariable UUID channelId, @RequestBody ChannelUpdateRequest request);

  @Operation(summary = "채널 삭제", description = "채널 ID로 채널을 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "삭제 성공"),
      @ApiResponse(responseCode = "404", description = "채널을 찾을 수 없음", content = @Content)
  })
  ResponseEntity<Void> delete(@PathVariable UUID channelId);
}
