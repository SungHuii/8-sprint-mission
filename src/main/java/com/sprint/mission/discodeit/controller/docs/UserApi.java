package com.sprint.mission.discodeit.controller.docs;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserSummaryResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdatePayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "사용자 관리 API")
public interface UserApi {

  @Operation(summary = "사용자 전체 조회", description = "등록된 모든 사용자의 목록을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  ResponseEntity<List<UserSummaryResponse>> findAll();

  @Operation(summary = "사용자 생성 (회원가입)", description = "새로운 사용자를 생성합니다. 프로필 이미지를 함께 업로드할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (필수 값 누락 등)", content = @Content),
      @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자 (이메일/닉네임 중복)", content = @Content)
  })
  ResponseEntity<UserResponse> create(
      @RequestPart("userCreateRequest") UserCreateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) throws IOException;

  @Operation(summary = "사용자 단건 조회", description = "ID로 특정 사용자를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
  })
  ResponseEntity<UserResponse> findById(@PathVariable UUID userId);

  @Operation(summary = "사용자 정보 수정", description = "사용자의 정보를 수정합니다. 변경할 필드만 전송할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "수정 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content),
      @ApiResponse(responseCode = "409", description = "이미 존재하는 닉네임/이메일", content = @Content)
  })
  ResponseEntity<UserResponse> update(
      @PathVariable UUID userId,
      @RequestPart(value = "userUpdateRequest", required = false) UserUpdateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) throws IOException;

  @Operation(summary = "사용자 삭제", description = "ID로 사용자를 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "삭제 성공"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
  })
  ResponseEntity<Void> delete(@PathVariable UUID userId);

  @Operation(summary = "사용자 상태 업데이트", description = "사용자의 마지막 활동 시간을 업데이트합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "업데이트 성공"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
  })
  ResponseEntity<UserStatusResponse> updateStatus(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdatePayload payload
  );
}
