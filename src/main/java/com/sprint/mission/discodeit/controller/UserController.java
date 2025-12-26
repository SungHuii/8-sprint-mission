package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public UserResponse create(@RequestParam String name,
                               @RequestParam String nickname,
                               @RequestParam String phoneNumber,
                               @RequestParam String password,
                               @RequestParam String email,
                               @RequestParam(required = false) String profileData,
                               @RequestParam(required = false) String profileContentType,
                               @RequestParam(required = false) String profileOriginalName) {
        BinaryContentCreateRequest profile = toBinaryContentRequest(
                profileData,
                profileContentType,
                profileOriginalName
        );
        UserCreateRequest request = new UserCreateRequest(
                name,
                nickname,
                phoneNumber,
                password,
                email,
                profile
        );
        return userService.create(request);
    }

    @RequestMapping(value = "/user/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAllUserDtos());
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    public UserResponse findById(@PathVariable UUID userId) {
        return userService.findById(userId);
    }

    @RequestMapping(value = "/users/{userId}/update", method = RequestMethod.GET)
    public UserResponse update(@PathVariable UUID userId,
                               @RequestParam(required = false) String name,
                               @RequestParam(required = false) String nickname,
                               @RequestParam(required = false) String phoneNumber,
                               @RequestParam(required = false) String password,
                               @RequestParam(required = false) String email,
                               @RequestParam(required = false) String newProfileData,
                               @RequestParam(required = false) String newProfileContentType,
                               @RequestParam(required = false) String newProfileOriginalName) {
        BinaryContentCreateRequest newProfile = toBinaryContentRequest(
                newProfileData,
                newProfileContentType,
                newProfileOriginalName
        );
        UserUpdateRequest boundRequest = new UserUpdateRequest(
                userId,
                name,
                nickname,
                phoneNumber,
                password,
                email,
                newProfile
        );
        return userService.update(boundRequest);
    }

    @RequestMapping(value = "/users/{userId}/delete", method = RequestMethod.GET)
    public void delete(@PathVariable UUID userId) {
        userService.deleteById(userId);
    }

    @RequestMapping(value = "/users/{userId}/status", method = RequestMethod.GET)
    public UserStatusResponse updateStatus(@PathVariable UUID userId,
                                           @RequestParam(required = false) String lastActiveAt) {
        UserStatusUpdateByUserIdRequest boundRequest =
                new UserStatusUpdateByUserIdRequest(userId, parseInstant(lastActiveAt));
        return userStatusService.updateByUserId(boundRequest);
    }

    private BinaryContentCreateRequest toBinaryContentRequest(String data,
                                                              String contentType,
                                                              String originalName) {
        if (data == null || data.isBlank()) {
            return null;
        }
        byte[] decoded = Base64.getDecoder().decode(data);
        return new BinaryContentCreateRequest(decoded, contentType, originalName);
    }

    private Instant parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Instant.parse(value);
    }
}
