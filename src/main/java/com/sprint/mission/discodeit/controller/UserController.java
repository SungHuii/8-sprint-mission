package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.docs.UserApi;
import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserSummaryResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdatePayload;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

  private static final String PART_USER_CREATE = "userCreateRequest";
  private static final String PART_USER_UPDATE = "userUpdateRequest";
  private static final String PART_PROFILE = "profile";

  private final UserService userService;
  private final UserStatusService userStatusService;

  @Override
  @GetMapping
  public ResponseEntity<List<UserSummaryResponse>> findAll() {
    return ResponseEntity.ok(userService.findAllUserSummaries());
  }

  @Override
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> create(
      @Valid @RequestPart(PART_USER_CREATE) UserCreateRequest request,
      @RequestPart(value = PART_PROFILE, required = false) MultipartFile profile
  ) throws IOException {
    BinaryContentCreateRequest binaryRequest = toBinaryContentRequest(profile);

    UserCreateRequest newRequest = new UserCreateRequest(
        request.email(),
        request.username(),
        request.password(),
        binaryRequest
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(newRequest));
  }

  @Override
  @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> update(
      @PathVariable UUID userId,
      @Valid @RequestPart(value = PART_USER_UPDATE, required = false) UserUpdateRequest request,
      @RequestPart(value = PART_PROFILE, required = false) MultipartFile profile
  ) throws IOException {
    BinaryContentCreateRequest binaryRequest = toBinaryContentRequest(profile);

    UserUpdateRequest newRequest = new UserUpdateRequest(
        request != null ? request.newUsername() : null,
        request != null ? request.newEmail() : null,
        request != null ? request.newPassword() : null,
        binaryRequest
    );

    return ResponseEntity.ok(userService.update(userId, newRequest));
  }

  @Override
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    userService.deleteById(userId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatusResponse> updateStatus(
      @PathVariable UUID userId,
      @Valid @RequestBody UserStatusUpdatePayload payload
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
        file.getOriginalFilename(),
        file.getSize(),
        file.getContentType(),
        file.getBytes()
    );
  }
}
