package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdatePayload;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private static final String PART_USER_CREATE = "userCreateRequest";
  private static final String PART_USER_UPDATE = "userUpdateRequest";
  private static final String PART_PROFILE = "profile";

  private final UserService userService;
  private final UserStatusService userStatusService;

  @Operation(summary = "사용자 전체 조회", description = "등록된 모든 사용자의 목록을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    return ResponseEntity.ok(userService.findAllUserDtos());
  }

  @Operation(summary = "사용자 생성 (회원가입)", description = "새로운 사용자를 생성합니다. 프로필 이미지를 함께 업로드할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (필수 값 누락 등)", content = @Content),
      @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자 (이메일/닉네임 중복)", content = @Content)
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> create(
      @RequestPart(PART_USER_CREATE) UserCreateRequest request,
      @RequestPart(value = PART_PROFILE, required = false) MultipartFile profile
  ) throws IOException {
    BinaryContentCreateRequest binaryRequest = toBinaryContentRequest(profile);

    // 프론트엔드 스펙에 맞춰 email, username, password만 받도록 변경
    UserCreateRequest newRequest = new UserCreateRequest(
        request.email(),
        request.username(),
        request.password(),
        binaryRequest
    );

    return ResponseEntity.ok(userService.create(newRequest));
  }

  @Operation(summary = "사용자 단건 조회", description = "사용자 ID로 특정 사용자를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
  })
  @GetMapping("/{userId}")
  public ResponseEntity<UserResponse> findById(@PathVariable UUID userId) {
    return ResponseEntity.ok(userService.findById(userId));
  }

  @Operation(summary = "사용자 정보 수정", description = "사용자의 정보를 수정합니다. 변경할 필드만 전송할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "수정 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content),
      @ApiResponse(responseCode = "409", description = "이미 존재하는 닉네임/이메일", content = @Content)
  })
  @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> update(
      @PathVariable UUID userId,
      @RequestPart(value = PART_USER_UPDATE, required = false) UserUpdateRequest request,
      @RequestPart(value = PART_PROFILE, required = false) MultipartFile profile
  ) throws IOException {
    BinaryContentCreateRequest binaryRequest = toBinaryContentRequest(profile);

    UserUpdateRequest newRequest = new UserUpdateRequest(
        userId,
        request != null ? request.name() : null,
        request != null ? request.nickname() : null,
        request != null ? request.phoneNumber() : null,
        request != null ? request.password() : null,
        request != null ? request.email() : null,
        binaryRequest
    );

    return ResponseEntity.ok(userService.update(newRequest));
  }

  @Operation(summary = "사용자 삭제", description = "사용자 ID로 사용자를 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "삭제 성공"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
  })
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    userService.deleteById(userId);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "사용자 상태 업데이트", description = "사용자의 마지막 활동 시간을 업데이트합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "업데이트 성공"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
  })
  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatusResponse> updateStatus(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdatePayload payload
  ) {
    String lastActiveAtStr = payload.newLastActiveAt();

    UserStatusUpdateByUserIdRequest request = new UserStatusUpdateByUserIdRequest(
        userId,
        lastActiveAtStr != null
            ? Instant.parse(lastActiveAtStr)
            : Instant.now()
    );

    return ResponseEntity.ok(userStatusService.updateByUserId(request));
  }

  private BinaryContentCreateRequest toBinaryContentRequest(MultipartFile file) throws IOException {
    if (file == null || file.isEmpty()) {
      return null;
    }
    return new BinaryContentCreateRequest(
        file.getBytes(),
        file.getContentType(),
        file.getOriginalFilename()
    );
  }
}
