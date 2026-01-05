package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private static final String PART_USER_CREATE = "userCreateRequest";
  private static final String PART_USER_UPDATE = "userUpdateRequest";
  private static final String PART_PROFILE = "profile";

  private final UserService userService;
  private final UserStatusService userStatusService;

  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    return ResponseEntity.ok(userService.findAllUserDtos());
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> create(
      @RequestPart(PART_USER_CREATE) UserCreateRequest request,
      @RequestPart(value = PART_PROFILE, required = false) MultipartFile profile
  ) throws IOException {
    BinaryContentCreateRequest binaryRequest = toBinaryContentRequest(profile);

    UserCreateRequest newRequest = new UserCreateRequest(
        request.name(),
        request.nickname(),
        request.phoneNumber(),
        request.password(),
        request.email(),
        binaryRequest
    );

    return ResponseEntity.ok(userService.create(newRequest));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserResponse> findById(@PathVariable UUID userId) {
    return ResponseEntity.ok(userService.findById(userId));
  }

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

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    userService.deleteById(userId);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatusResponse> updateStatus(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest payload
  ) {
    String lastActiveAtStr = payload.newLastActiveAt();

    UserStatusUpdateByUserIdRequest request = new UserStatusUpdateByUserIdRequest(
        userId,
        lastActiveAtStr != null
            ? java.time.Instant.parse(lastActiveAtStr)
            : java.time.Instant.now()
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
