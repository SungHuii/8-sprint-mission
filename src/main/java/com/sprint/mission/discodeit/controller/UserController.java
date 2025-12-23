package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public UserResponse create(@RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public UserResponse findById(@PathVariable UUID userId) {
        return userService.findById(userId);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
    public UserResponse update(@PathVariable UUID userId, @RequestBody UserUpdateRequest request) {
        UserUpdateRequest boundRequest = new UserUpdateRequest(
                userId,
                request.name(),
                request.nickname(),
                request.phoneNumber(),
                request.password(),
                request.email(),
                request.newProfile()
        );
        return userService.update(boundRequest);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID userId) {
        userService.deleteById(userId);
    }

    @RequestMapping(value = "/{userId}/status", method = RequestMethod.PUT)
    public UserStatusResponse updateStatus(@PathVariable UUID userId,
                                           @RequestBody UserStatusUpdateByUserIdRequest request) {
        UserStatusUpdateByUserIdRequest boundRequest =
                new UserStatusUpdateByUserIdRequest(userId, request.lastActiveAt());
        return userStatusService.updateByUserId(boundRequest);
    }
}
