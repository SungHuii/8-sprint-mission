package com.sprint.mission.discodeit.controller.docs;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserSummaryResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdatePayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "사용자 관리 API")
public interface UserApi {

  @Operation(summary = "전체 사용자 조회", description = "등록된 모든 사용자의 요약 정보를 조회합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  ResponseEntity<List<UserSummaryResponse>> findAll();

  @Operation(summary = "사용자 등록 (회원가입)", description = "새로운 사용자를 등록합니다. 프로필 이미지를 포함할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "등록 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
      @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자명 또는 이메일", content = @Content)
  })
  ResponseEntity<UserResponse> create(
      @Parameter(description = "사용자 생성 요청 데이터 (JSON)", required = true, schema = @Schema(implementation = UserCreateRequest.class))
      @RequestPart("userCreateRequest") UserCreateRequest request,
      @Parameter(description = "프로필 이미지 파일 (Optional)")
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) throws IOException;

  @Operation(summary = "사용자 정보 수정", description = "사용자의 정보를 수정합니다. 변경할 필드만 전송하면 됩니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "수정 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content),
      @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자명 또는 이메일", content = @Content)
  })
  ResponseEntity<UserResponse> update(
      @Parameter(description = "사용자 ID", required = true) @PathVariable UUID userId,
      @Parameter(description = "사용자 수정 요청 데이터 (JSON)", required = true, schema = @Schema(implementation = UserUpdateRequest.class))
      @RequestPart(value = "userUpdateRequest", required = false) UserUpdateRequest request,
      @Parameter(description = "새 프로필 이미지 파일 (Optional)")
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) throws IOException;

  @Operation(summary = "사용자 삭제 (회원탈퇴)", description = "사용자를 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "삭제 성공"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "사용자 ID", required = true) @PathVariable UUID userId
  );

  @Operation(summary = "사용자 상태 업데이트", description = "사용자의 상태(마지막 활동 시간)를 업데이트합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "업데이트 성공"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
  })
  ResponseEntity<UserStatusResponse> updateStatus(
      @Parameter(description = "사용자 ID", required = true) @PathVariable UUID userId,
      @RequestBody UserStatusUpdatePayload payload
  );
}
